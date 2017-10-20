package uk.gov.ida.verifyserviceprovider.factories.metadata;

import org.apache.commons.lang.StringUtils;
import uk.gov.ida.verifyserviceprovider.utils.ManifestReader;

import java.util.List;

public class MetadataUserAgentFactory {
    private final ManifestReader manifestReader;
    private final List<String> entityIds;

    public MetadataUserAgentFactory(ManifestReader manifestReader, List<String> entityIds) {
        this.manifestReader = manifestReader;
        this.entityIds = entityIds;
    }

    public String getUserAgent() {
        String entityString = StringUtils.join(entityIds, ",");
        String version = manifestReader.getVersion();
        String userAgentString = String.format("VerifyServiceProvider/%s/%s", entityString , version);
        return userAgentString;
    }

}
