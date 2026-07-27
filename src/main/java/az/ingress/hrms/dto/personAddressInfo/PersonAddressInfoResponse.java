package az.ingress.hrms.dto.personAddressInfo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonAddressInfoResponse {

    private Integer id;
    private Integer personId;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
