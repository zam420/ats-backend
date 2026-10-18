package com.deepconnect.ats.common.microsoft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

/**
 * Minimal Microsoft Graph client (client-credentials flow) using ats.graph.* settings.
 * Replaces the Django ats.common.microsoft.graph_api package and the exchangelib mailbox access.
 */
@Component
public class GraphClient {

    private final String tenantId;
    private final String clientId;
    private final String clientSecret;
    private final String endpoint;
    private final String scope;
    private final RestClient http = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    private volatile String token;
    private volatile Instant tokenExpiry = Instant.EPOCH;

    public GraphClient(@Value("${ats.graph.tenant-id:}") String tenantId,
                       @Value("${ats.graph.client-id:}") String clientId,
                       @Value("${ats.graph.client-secret:}") String clientSecret,
                       @Value("${ats.graph.endpoint}") String endpoint,
                       @Value("${ats.graph.scopes}") String scope) {
        this.tenantId = tenantId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.endpoint = endpoint;
        this.scope = scope;
    }

    private synchronized String accessToken() {
        if (token != null && Instant.now().isBefore(tokenExpiry.minusSeconds(60))) {
            return token;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", scope);
        JsonNode rsp = http.post()
                .uri("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(JsonNode.class);
        token = rsp.get("access_token").asText();
        tokenExpiry = Instant.now().plusSeconds(rsp.get("expires_in").asLong());
        return token;
    }

    /** GET {endpoint}{path}; path may already contain a query string. Absolute URLs (e.g. @odata.nextLink) are accepted. */
    public JsonNode get(String pathOrUrl) {
        String url = pathOrUrl.startsWith("http") ? pathOrUrl : endpoint + pathOrUrl;
        return http.get().uri(url)
                .header("Authorization", "Bearer " + accessToken())
                .header("Prefer", "outlook.timezone=\"Asia/Shanghai\"")
                .retrieve().body(JsonNode.class);
    }

    public JsonNode post(String path, Object body) {
        return http.post().uri(endpoint + path)
                .header("Authorization", "Bearer " + accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.valueToTree(body))
                .retrieve().body(JsonNode.class);
    }

    public void postNoContent(String path, Object body) {
        http.post().uri(endpoint + path)
                .header("Authorization", "Bearer " + accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.valueToTree(body))
                .retrieve().toBodilessEntity();
    }

    public JsonNode patch(String path, Map<String, Object> body) {
        return http.patch().uri(endpoint + path)
                .header("Authorization", "Bearer " + accessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.valueToTree(body))
                .retrieve().body(JsonNode.class);
    }
}
