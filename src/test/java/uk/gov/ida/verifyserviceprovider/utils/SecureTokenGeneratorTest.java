package uk.gov.ida.verifyserviceprovider.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SecureTokenGeneratorTest {

    private static String STATIC_REQUEST_ID = "a7a2680c-67af-4afe-ad4a-78dd3d53c285";

    private static String STATIC_SECURE_TOKEN_KEY = "NeverEatChipsEatSaladSandwiches";

    @Test
    public void shouldGenerateSecureToken() {

        String expectedOutput = "4ebb8f5677fa03765a730343f40f17c2e93ee122fc6af29951d11325302a2c03";
        String actualOutput = SecureTokenGenerator.generate(STATIC_SECURE_TOKEN_KEY, STATIC_REQUEST_ID);

        assertThat(actualOutput).isEqualTo(expectedOutput);
    }
}
