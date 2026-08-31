package az.ingress.hrms.log.person.personPhoto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Person_Photos_Log", schema = "log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPhotoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "main_id", nullable = false)
    private Integer mainId;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "is_main")
    private Boolean isMain;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
