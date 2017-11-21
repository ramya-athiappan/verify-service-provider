package uk.gov.ida.verifyserviceprovider.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.cert.Certificate;

public class MsaConfiguration {

    @JsonProperty
    @NotNull
    @Valid
    @JsonDeserialize(using = CertificateDeserializer.class)
    private Certificate primarySigningCertificate;

    @JsonProperty
    @Valid
    @JsonDeserialize(using = CertificateDeserializer.class)
    private Certificate secondarySigningCertificate;

    public Certificate getPrimarySigningCertificate() {
        return primarySigningCertificate;
    }

    public Certificate getSecondarySigningCertificate() {
        return secondarySigningCertificate;
    }
}
