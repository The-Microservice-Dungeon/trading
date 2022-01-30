package dungeon.trading.core;

import net.minidev.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<?> post(String url, JSONObject requestPayload, Class<?> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<JSONObject> entity = new HttpEntity<>(requestPayload, headers);
        return this.restTemplate.postForEntity(url, entity, responseType);
    }

    public ResponseEntity<?> get(String url, Class<?> responseType, int urlParams) {
        return this.restTemplate.getForEntity(url, responseType, urlParams);
    }
}
