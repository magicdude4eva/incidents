package com.example.incidents.es;

import co.elastic.clients.transport.TransportUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * Configures and creates a client for elastic search using the configuration given in "application.properties".
 * The ES-client is a singleton.
 */
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class ESClientFactory {

    final static Logger LOGGER = LoggerFactory.getLogger(ESClientFactory.class);

    @Autowired
    ConfigProperties configProperties;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ESClient create() {
        final var host = HttpHost.create(configProperties.endpoint());
        final var usernamePasswordCredentials =
                new UsernamePasswordCredentials(configProperties.username(), configProperties.password());
        final var credentials = new BasicCredentialsProvider();
        credentials.setCredentials(AuthScope.ANY, usernamePasswordCredentials);

        SSLContext sslContext = null;
        try {
            sslContext = TransportUtils.sslContextFromHttpCaCrt(new ClassPathResource("http_ca.crt").getInputStream());
        } catch (IOException ioException) {
            LOGGER.error(String.format("Unable to create SSL context: %s", ioException.getMessage()), ioException);
        }

        return new ESClient(host, credentials, sslContext);
    }

}
