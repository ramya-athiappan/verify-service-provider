package uk.gov.ida.verifyserviceprovider;

import com.google.common.collect.ImmutableMap;
import common.uk.gov.ida.verifyserviceprovider.servers.MockMsaServer;
import org.json.JSONObject;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.ida.verifyserviceprovider.dto.ErrorBody;
import uk.gov.ida.verifyserviceprovider.dto.RequestResponseBody;
import uk.gov.ida.verifyserviceprovider.rules.VerifyServiceProviderAppRule;
import uk.gov.ida.verifyserviceprovider.services.ComplianceToolService;
import uk.gov.ida.verifyserviceprovider.services.GenerateRequestService;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.TEST_RP_MS_PRIVATE_SIGNING_KEY;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.TEST_RP_PUBLIC_ENCRYPTION_CERT;
import static uk.gov.ida.verifyserviceprovider.builders.ComplianceToolInitialisationRequestBuilder.aComplianceToolInitialisationRequest;
import static uk.gov.ida.verifyserviceprovider.dto.Scenario.ACCOUNT_CREATION;

public class UserAccountCreationResponseAcceptanceTest {

    @ClassRule
    public static MockMsaServer msaServer = new MockMsaServer();

    @ClassRule
    public static VerifyServiceProviderAppRule application = new VerifyServiceProviderAppRule(msaServer);

    private static Client client = application.client();
    private static ComplianceToolService complianceTool = new ComplianceToolService(client);
    private static GenerateRequestService generateRequestService = new GenerateRequestService(client);

    @Test
    public void shouldHandleAUserAccountCreationResponse() {
        Response response = getResponse("FIRST_NAME",
            "FIRST_NAME_VERIFIED",
            "DATE_OF_BIRTH",
            "DATE_OF_BIRTH_VERIFIED",
            "CURRENT_ADDRESS",
            "CURRENT_ADDRESS_VERIFIED",
            "CYCLE_3");
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
        assertThat(jsonResponse.getString("scenario")).isEqualTo(ACCOUNT_CREATION.name());
        assertThat(jsonResponse.getString("pid")).isEqualTo("some-expected-pid");
        assertThat(jsonResponse.keys()).contains("attributes");
    }

    @Test
    public void shouldOnlyReturnRequestedAttributes() {
        Response response = getResponse("FIRST_NAME", "FIRST_NAME_VERIFIED", "DATE_OF_BIRTH", "DATE_OF_BIRTH_VERIFIED");
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
        JSONObject attributes = jsonResponse.getJSONObject("attributes");

        assertThat(attributes.keys()).containsExactly("firstName", "dateOfBirth");
    }

    @Test
    public void dateFormatIsISO8601() {
        Response response = getResponse("DATE_OF_BIRTH", "DATE_OF_BIRTH_VERIFIED");

        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());

        JSONObject jsonResponse = new JSONObject(response.readEntity(String.class));
        JSONObject attributes = jsonResponse.getJSONObject("attributes");

        String dob = attributes.getJSONObject("dateOfBirth").getString("value");
        assertThat(dob).matches(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$"));
    }

    @Test
    public void shouldErrorIfOnlyVerifiedIsRequested() {
        Response response = getResponse("DATE_OF_BIRTH_VERIFIED");
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

        ErrorBody errorResponse = response.readEntity(ErrorBody.class);

        assertThat(errorResponse.getMessage()).isEqualTo("Invalid attributes request: Cannot request verification status without requesting attribute value");
    }

    @Test
    public void shouldErrorIfVerifiedIsNotRequested() {
        Response response = getResponse("SURNAME");
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());

        ErrorBody errorResponse = response.readEntity(ErrorBody.class);

        assertThat(errorResponse.getMessage()).isEqualTo("Invalid attributes request: Cannot request attribute without requesting verification status. Please check your MSA configuration settings.");
    }

    private Response getResponse(String... attributes) {
        complianceTool.initialiseWith(
            aComplianceToolInitialisationRequest()
                .withMatchingServiceSigningPrivateKey(TEST_RP_MS_PRIVATE_SIGNING_KEY)
                .withMatchingServiceEntityId(MockMsaServer.MSA_ENTITY_ID)
                .withEncryptionCertificate(TEST_RP_PUBLIC_ENCRYPTION_CERT)
                .withExpectedPid("some-expected-pid")
                .withUserAccountCreationAttributes(Arrays.asList(attributes))
                .build()
        );

        RequestResponseBody requestResponseBody = generateRequestService.generateAuthnRequest(application.getLocalPort());

        Map<String, String> translateResponseRequestData = ImmutableMap.of(
            "samlResponse", complianceTool.createUserAccountCreationResponseFor(requestResponseBody.getSamlRequest()),
            "requestId", requestResponseBody.getRequestId()
        );

        Response response = client
            .target(String.format("http://localhost:%d/translate-response", application.getLocalPort()))
            .request()
            .buildPost(json(translateResponseRequestData))
            .invoke();

        return response;
    }
}