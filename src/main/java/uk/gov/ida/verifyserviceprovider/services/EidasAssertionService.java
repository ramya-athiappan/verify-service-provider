package uk.gov.ida.verifyserviceprovider.services;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import uk.gov.ida.saml.core.transformers.MatchingDatasetUnmarshaller;
import uk.gov.ida.saml.metadata.MetadataResolverRepository;
import uk.gov.ida.saml.security.MetadataBackedSignatureValidator;
import uk.gov.ida.saml.security.SamlAssertionsSignatureValidator;
import uk.gov.ida.saml.security.SamlMessageSignatureValidator;
import uk.gov.ida.verifyserviceprovider.dto.LevelOfAssurance;
import uk.gov.ida.verifyserviceprovider.dto.NonMatchingAttributes;
import uk.gov.ida.verifyserviceprovider.dto.TranslatedNonMatchingResponseBody;
import uk.gov.ida.verifyserviceprovider.exceptions.SamlResponseValidationException;
import uk.gov.ida.verifyserviceprovider.mappers.MatchingDatasetToNonMatchingAttributesMapper;
import uk.gov.ida.verifyserviceprovider.validators.ConditionsValidator;
import uk.gov.ida.verifyserviceprovider.validators.InstantValidator;
import uk.gov.ida.verifyserviceprovider.validators.SubjectValidator;

import java.util.List;

import static java.util.Collections.singletonList;
import static uk.gov.ida.verifyserviceprovider.dto.NonMatchingScenario.IDENTITY_VERIFIED;

public class EidasAssertionService extends AssertionServiceV2 {

    private final InstantValidator instantValidator;
    private final ConditionsValidator conditionsValidator;
    private final MetadataResolverRepository metadataResolverRepository;


    public EidasAssertionService(
            SubjectValidator subjectValidator,
            MatchingDatasetUnmarshaller matchingDatasetUnmarshaller,
            MatchingDatasetToNonMatchingAttributesMapper mdsMapper,
            InstantValidator instantValidator,
            ConditionsValidator conditionsValidator,
            MetadataResolverRepository metadataResolverRepository
    ) {
        super(subjectValidator, matchingDatasetUnmarshaller, mdsMapper);
        this.instantValidator = instantValidator;
        this.conditionsValidator = conditionsValidator;
        this.metadataResolverRepository = metadataResolverRepository;
    }


    @Override
    public TranslatedNonMatchingResponseBody translateSuccessResponse(List<Assertion> assertions, String expectedInResponseTo, LevelOfAssurance expectedLevelOfAssurance, String entityId) {
        if (assertions.size() != 1) {
            throw new SamlResponseValidationException("Exactly one country assertion is expected.");
        }

        Assertion countryAssertion = assertions.get(0);

        validateCountryAssertion(countryAssertion, expectedInResponseTo, entityId);

        LevelOfAssurance levelOfAssurance = extractLevelOfAssuranceFrom(countryAssertion);

        String nameID = getNameIdFrom(countryAssertion);

        NonMatchingAttributes attributes = translateAttributes(countryAssertion);

        return new TranslatedNonMatchingResponseBody(IDENTITY_VERIFIED, nameID, levelOfAssurance, attributes);
    }

    private void validateCountryAssertion(Assertion assertion, String expectedInResponseTo, String entityId) {
        metadataResolverRepository.getSignatureTrustEngine(assertion.getIssuer().getValue())
                .map(MetadataBackedSignatureValidator::withoutCertificateChainValidation)
                .map(SamlMessageSignatureValidator::new)
                .map(SamlAssertionsSignatureValidator::new)
                .orElseThrow(() -> new SamlResponseValidationException("Unable to find metadata resolver for entity Id " + assertion.getIssuer().getValue()))
                .validate(singletonList(assertion), IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        instantValidator.validate(assertion.getIssueInstant(), "Country Assertion IssueInstant");
        subjectValidator.validate(assertion.getSubject(), expectedInResponseTo);
        conditionsValidator.validate(assertion.getConditions(), entityId);
    }

    public LevelOfAssurance extractLevelOfAssuranceFrom(Assertion countryAssertion) {
        String levelOfAssuranceString = extractLevelOfAssuranceStringFrom(countryAssertion);

        try {
            return LevelOfAssurance.fromSamlValue(levelOfAssuranceString);  //TODO: If this works as-is, then we should move the method to the base class.
        } catch (Exception ex) {
            throw new SamlResponseValidationException(String.format("Level of assurance '%s' is not supported.", levelOfAssuranceString));
        }
    }

    public Boolean isCountryAssertion(Assertion assertion) {
        return metadataResolverRepository.getResolverEntityIds().contains(assertion.getIssuer().getValue());
    }

}
