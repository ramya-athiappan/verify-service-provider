package uk.gov.ida.verifyserviceprovider.utils;

import org.apache.commons.codec.digest.HmacUtils;

import javax.validation.constraints.NotNull;

public class SecureTokenGenerator {

    public static String generate(@NotNull String secureTokenKey, @NotNull String requestId) {
        return HmacUtils.hmacSha256Hex(secureTokenKey, requestId);
    }
}
