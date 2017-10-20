package unit.uk.gov.ida.verifyserviceprovider.factories.metadata;

import org.junit.Test;
import uk.gov.ida.verifyserviceprovider.factories.metadata.MetadataUserAgentFactory;
import uk.gov.ida.verifyserviceprovider.utils.ManifestReader;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataUserAgentFactoryTest {
    @Test
    public void theUserAgentContainsTheVersionNumberAndEntityIds() throws Exception {
        MetadataUserAgentFactory metadataUserAgentFactory = new MetadataUserAgentFactory(new ManifestReader(), asList("FOO", "BAR", "BAZ"));
        assertThat(metadataUserAgentFactory.getUserAgent()).isEqualTo("VerifyServiceProvider/FOO,BAR,BAZ/UNKNOWN_VERSION");
    }

    @Test
    public void theUserAgentContainsTheVersionNumberAndEntityId() throws Exception {
        MetadataUserAgentFactory metadataUserAgentFactory = new MetadataUserAgentFactory(new ManifestReader(), asList("FOO"));
        assertThat(metadataUserAgentFactory.getUserAgent()).isEqualTo("VerifyServiceProvider/FOO/UNKNOWN_VERSION");
    }

}