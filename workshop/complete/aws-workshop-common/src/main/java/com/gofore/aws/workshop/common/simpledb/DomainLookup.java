package com.gofore.aws.workshop.common.simpledb;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.gofore.aws.workshop.common.properties.ApplicationProperties;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DomainLookup {
    
    private final SimpleDBClient simpleDBClient;
    private final String imagesDomainPrefix;

    @Inject
    public DomainLookup(ApplicationProperties properties, SimpleDBClient simpleDBClient) {
        this.simpleDBClient = simpleDBClient;
        this.imagesDomainPrefix = getImagesDomainPrefix(properties.lookup("aws.user"));
    }

    public String getImagesDomain() {
        return simpleDBClient.listDomains(new ListDomainsRequest())
                .thenApply(ListDomainsResult::getDomainNames)
                .thenApply(Collection::stream)
                .thenApply(s -> s.filter(d -> d.startsWith(imagesDomainPrefix)))
                .thenApply(Stream::findFirst)
                .thenApply(Optional::get)
                .join();
    }
    
    private String getImagesDomainPrefix(String user) {
        return "aws-workshop-" + user + "-Images-";
    }
}
