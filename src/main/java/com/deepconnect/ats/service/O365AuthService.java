package com.deepconnect.ats.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class O365AuthService {
    private final String configurationUrl;
    private final RestClient http = RestClient.create();
    @Getter private volatile String issuer;
    @Getter private volatile List<JsonNode> jwks = List.of();

    public O365AuthService(@Value("${ats.azure.oauth-config-url:}") String configurationUrl) {
        this.configurationUrl = configurationUrl;
    }

    public synchronized void refresh() {
        if (configurationUrl == null || configurationUrl.isBlank()) throw new IllegalStateException("ats.azure.oauth-config-url is not configured");
        JsonNode configuration = http.get().uri(configurationUrl).retrieve().body(JsonNode.class);
        String nextIssuer = configuration.path("issuer").asText(null);
        String jwksUri = configuration.path("jwks_uri").asText(null);
        if (nextIssuer == null || jwksUri == null) {
            throw new IllegalStateException("OIDC configuration is missing issuer or jwks_uri");
        }
        JsonNode keys = http.get().uri(jwksUri).retrieve().body(JsonNode.class).path("keys");
        if (!keys.isArray()) throw new IllegalStateException("OIDC JWKS response has no keys array");
        List<JsonNode> nextKeys = new ArrayList<>();
        keys.forEach(nextKeys::add);
        this.issuer = nextIssuer;
        this.jwks = List.copyOf(nextKeys);
        log.info("o365 ========> init_o365_auth()");
    }
}
