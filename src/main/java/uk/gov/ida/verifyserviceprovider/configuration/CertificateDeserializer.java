package uk.gov.ida.verifyserviceprovider.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;

import static java.text.MessageFormat.format;
import static uk.gov.ida.verifyserviceprovider.utils.DefaultObjectMapper.OBJECT_MAPPER;

public class CertificateDeserializer extends JsonDeserializer<Certificate> {

    @Override
    public Certificate deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        jsonParser.setCodec(OBJECT_MAPPER);
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            return CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(node.asText())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
