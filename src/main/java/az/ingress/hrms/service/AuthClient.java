package az.ingress.hrms.service;

import az.ingress.hrms.dto.auth.CheckAccessRequest;

public interface AuthClient {

    boolean checkAccess(
            String authorizationHeader,
            CheckAccessRequest request
    );
}