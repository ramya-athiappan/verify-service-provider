package uk.gov.ida.verifyserviceprovider.utils;

import com.google.common.collect.ImmutableList;
import uk.gov.ida.verifyserviceprovider.configuration.VerifyServiceProviderConfiguration;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Optional;

import static com.google.common.io.BaseEncoding.base16;

public class PublicKeyChecksumsFormatter {
    public static String format(VerifyServiceProviderConfiguration configuration) {
        return StringTableFormatter.format(
            100,
            "Public Key Checksums: \n" +
            "( see https://github.com/alphagov/verify-service-provider/tree/master/docs/troubleshooting/cryptography-issues.md )",
            ImmutableList.of(
                "samlSigningPublicKey             - " + getPublicKeyChecksum(configuration.getSamlSigningKey()).orElse("null"),
                "samlPrimaryEncryptionPublicKey   - " + getPublicKeyChecksum(configuration.getSamlPrimaryEncryptionKey()).orElse("null"),
                "samlSecondaryEncryptionPublicKey - " + getPublicKeyChecksum(configuration.getSamlSecondaryEncryptionKey()).orElse("null")
            )
        );
    }

    private static Optional<String> getPublicKeyChecksum(PrivateKey privateKey) {
        return Optional.ofNullable(privateKey)
            .map(Crypto::publicKeyFromPrivateKey)
            .map(Key::getEncoded)
            .map(PublicKeyChecksumsFormatter::getChecksum);
    }

    private static String getChecksum(byte[] input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(input);
        return base16().lowerCase().encode(digest.digest());
    }
}
