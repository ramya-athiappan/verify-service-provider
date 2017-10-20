package uk.gov.ida.verifyserviceprovider.factories.metadata;

import io.dropwizard.setup.Environment;
import uk.gov.ida.saml.metadata.MetadataResolverConfiguration;
import uk.gov.ida.saml.metadata.factories.MetadataClientFactory;

import javax.ws.rs.client.Client;
import java.util.Optional;

public class VersionedMetadataClientFactory extends MetadataClientFactory {
    private MetadataUserAgentFactory metadataUserAgentFactory;

    @Override
    public Client getClient(Environment environment, MetadataResolverConfiguration metadataConfiguration) {
        if(!metadataConfiguration.getJerseyClientConfiguration().getUserAgent().isPresent()) {
            metadataConfiguration.getJerseyClientConfiguration().setUserAgent(Optional.of(metadataUserAgentFactory.getUserAgent()));
        }
        return super.getClient(environment, metadataConfiguration);
    }

    public VersionedMetadataClientFactory(MetadataUserAgentFactory metadataUserAgentFactory) {
        super();
        this.metadataUserAgentFactory = metadataUserAgentFactory;
    }
}
