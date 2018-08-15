package unit.uk.gov.ida.verifyserviceprovider.validators;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Answers;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Subject;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.verifyserviceprovider.exceptions.SamlResponseValidationException;
import uk.gov.ida.verifyserviceprovider.validators.AssertionValidator;
import uk.gov.ida.verifyserviceprovider.validators.ConditionsValidator;
import uk.gov.ida.verifyserviceprovider.validators.InstantValidator;
import uk.gov.ida.verifyserviceprovider.validators.SubjectValidator;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.saml.core.test.builders.AttributeStatementBuilder.anAttributeStatement;
import static uk.gov.ida.saml.core.test.builders.AuthnStatementBuilder.anAuthnStatement;

public class AssertionValidatorTest {

    private AssertionValidator validator;

    private InstantValidator instantValidator;
    private SubjectValidator subjectValidator;
    private ConditionsValidator conditionsValidator;
    private Assertion assertion;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        instantValidator = mock(InstantValidator.class);
        subjectValidator = mock(SubjectValidator.class);
        conditionsValidator = mock(ConditionsValidator.class);
        assertion = mock(Assertion.class);
        AuthnStatement authnStatement = mock(AuthnStatement.class);

        validator = new AssertionValidator(
            instantValidator,
            subjectValidator,
            conditionsValidator
        );

        when(assertion.getAuthnStatements()).thenReturn(ImmutableList.of(authnStatement));

        IdaSamlBootstrap.bootstrap();
    }

    @Test
    public void shouldValidateAuthnStatementAssertionIssueInstant() {
        DateTime issueInstant = new DateTime();
        when(assertion.getIssueInstant()).thenReturn(issueInstant);

        validator.validateAuthnStatementAssertion(assertion, "any-expected-in-response-to", "any-entity-id");

        verify(instantValidator).validate(issueInstant, "Assertion IssueInstant");
    }

    @Test
    public void shouldValidateAuthnStatementAssertionSubject() {
        Subject subject = mock(Subject.class, Answers.RETURNS_DEEP_STUBS);
        when(assertion.getSubject()).thenReturn(subject);
        when(subject.getNameID().getValue()).thenReturn("any-value");

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");

        verify(subjectValidator).validate(subject, "some-expected-in-response-to");
    }

    @Test
    public void shouldValidateAuthnStatementAssertionConditions() {
        Conditions conditions = mock(Conditions.class);
        when(assertion.getConditions()).thenReturn(conditions);

        validator.validateAuthnStatementAssertion(assertion, "any-expected-in-response-to", "some-entity-id");

        verify(conditionsValidator).validate(conditions, "some-entity-id");
    }

    @Test
    public void shouldThrowExceptionIfAuthnStatementsIsNull() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one authn statement is expected.");

        when(assertion.getAuthnStatements()).thenReturn(null);

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void shouldThrowExceptionIfAuthnStatementsIsEmpty() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one authn statement is expected.");

        when(assertion.getAuthnStatements()).thenReturn(Collections.emptyList());

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void shouldThrowExceptionIfMoreThanOneAuthnStatements() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one authn statement is expected.");

        when(assertion.getAuthnStatements()).thenReturn(ImmutableList.of(
            anAuthnStatement().build(),
            anAuthnStatement().build()
        ));

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }
    
    @Test
    public void validateAuthnStatementAssertion_shouldThrowException_whenAuthnStatementsIsNull() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one authn statement is expected.");

        when(assertion.getAuthnStatements()).thenReturn(null);

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void validateAuthnStatementAssertion_shouldThrowException_whenAuthnStatementsIsEmpty() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one authn statement is expected.");

        when(assertion.getAuthnStatements()).thenReturn(ImmutableList.of());

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void validateAuthnStatementAssertion_shouldThrowException_whenNotExactlyOneAuthnStatementExists() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one authn statement is expected.");

        when(assertion.getAuthnStatements()).thenReturn(ImmutableList.of(
                anAuthnStatement().build(),
                anAuthnStatement().build()
        ));

        validator.validateAuthnStatementAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void shouldValidateAuthnStatementAssertionAuthnInstant() {
        DateTime issueInstant = new DateTime();
        when(assertion.getAuthnStatements().get(0).getAuthnInstant()).thenReturn(issueInstant);

        validator.validateAuthnStatementAssertion(assertion, "any-expected-in-response-to", "any-entity-id");

        verify(instantValidator).validate(issueInstant, "Assertion AuthnInstant");
    }

    @Test
    public void validateMatchingDatasetAssertion_shouldThrowException_whenAttributeStatementsIsNull() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one attribute statement is expected.");

        when(assertion.getAttributeStatements()).thenReturn(null);

        validator.validateMatchingDatasetAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void validateMatchingDatasetAssertion_shouldThrowException_whenAttributeStatementsIsEmpty() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one attribute statement is expected.");

        when(assertion.getAttributeStatements()).thenReturn(ImmutableList.of());

        validator.validateMatchingDatasetAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }

    @Test
    public void validateMatchingDatasetAssertion_shouldThrowException_whenNotExactlyOneAttributeStatementExists() {
        expectedException.expect(SamlResponseValidationException.class);
        expectedException.expectMessage("Exactly one attribute statement is expected.");

        when(assertion.getAttributeStatements()).thenReturn(ImmutableList.of(
                anAttributeStatement().build(),
                anAttributeStatement().build()
        ));

        validator.validateMatchingDatasetAssertion(assertion, "some-expected-in-response-to", "any-entity-id");
    }
}