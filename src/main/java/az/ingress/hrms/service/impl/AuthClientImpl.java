package az.ingress.hrms.service.impl;

import az.ingress.hrms.dto.auth.CheckAccessRequest;
import az.ingress.hrms.service.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthClientImpl implements AuthClient {

    private final RestTemplate restTemplate;

    @Override
    public boolean checkAccess(
            String authorizationHeader,
            CheckAccessRequest request
    ) {

        HttpHeaders headers =
                new HttpHeaders();
        // for sending json files
        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        /*
         * Forward the SAME JWT that the client
         * used to access HR Server.
         */
        headers.set(
                HttpHeaders.AUTHORIZATION,
                authorizationHeader
        );

        HttpEntity<CheckAccessRequest> entity =
                new HttpEntity<>(
                        request,
                        headers
                );

        ResponseEntity<Boolean> response =
                restTemplate.exchange(
                        "http://localhost:8080/api/v1/auth/check-access",
                        HttpMethod.POST,
                        entity,
                        Boolean.class
                );

        return Boolean.TRUE.equals(
                response.getBody()
        );
    }
}