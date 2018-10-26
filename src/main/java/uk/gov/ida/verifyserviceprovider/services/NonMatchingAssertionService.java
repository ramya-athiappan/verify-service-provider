package uk.gov.ida.verifyserviceprovider.services;


import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.StatusCode;
import uk.gov.ida.saml.core.validators.assertion.AssertionAttributeStatementValidator;
import uk.gov.ida.saml.security.SamlAssertionsSignatureValidator;
import uk.gov.ida.verifyserviceprovider.dto.LevelOfAssurance;
import uk.gov.ida.verifyserviceprovider.dto.TranslatedResponseBody;
import uk.gov.ida.verifyserviceprovider.validators.SubjectValidator;

import java.util.List;

public class NonMatchingAssertionService  extends ValidateAssertion
        implements TranslatedResultResponse , ResponseBodyTranslator {


    public NonMatchingAssertionService(
            SamlAssertionsSignatureValidator assertionsSignatureValidator,
            SubjectValidator subjectValidator,
            AssertionAttributeStatementValidator attributeStatementValidator ) {

            super(assertionsSignatureValidator, subjectValidator, attributeStatementValidator);

    }

    @Override
    public TranslatedResultResponse getTranslatedResultResponse() {
        return this;
    }

    @Override
    public TranslatedResponseBody translateNonSuccessResponse( StatusCode statusCode ) {

        return null;
    }
    @Override
    public TranslatedResponseBody translateSuccessResponse( List<Assertion> assertions,
                                                            String expectedInResponseTo,
                                                            LevelOfAssurance expectedLevelOfAssurance,
                                                            String entityId ) {
        doValidation(assertions, expectedInResponseTo, expectedLevelOfAssurance);
        return translateAssertions(assertions);
    }

    private TranslatedResponseBody translateAssertions( List<Assertion> assertions ) {
        return null;
    }
}
