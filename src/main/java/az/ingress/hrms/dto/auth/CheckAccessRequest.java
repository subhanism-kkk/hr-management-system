package az.ingress.hrms.dto.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckAccessRequest {

    private String url;

    private String method;
}