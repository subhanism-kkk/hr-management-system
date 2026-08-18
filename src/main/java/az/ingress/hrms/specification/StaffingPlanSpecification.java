package az.ingress.hrms.specification;

import az.ingress.hrms.entity.organization.StaffingPlan;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class StaffingPlanSpecification {

    public static Specification<StaffingPlan> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("structure").get("name")), pattern),
                cb.like(cb.lower(root.get("position").get("name")), pattern)
        );
    }

    public static Specification<StaffingPlan> hasStructureId(Integer structureId) {
        if (structureId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("structure").get("id"), structureId);
    }

    public static Specification<StaffingPlan> hasPositionId(Integer positionId) {
        if (positionId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("position").get("id"), positionId);
    }

    public static Specification<StaffingPlan> isClosed(Boolean isClosed) {
        if (isClosed == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("isClosed"), isClosed);
    }

    public static Specification<StaffingPlan> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(
                cb.lower(root.get("status").get("code")),
                statusCode.trim().toLowerCase()
        );
    }

    public static Specification<StaffingPlan> createdFrom(LocalDateTime createdFrom) {
        if (createdFrom == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom);
    }

    public static Specification<StaffingPlan> createdTo(LocalDateTime createdTo) {
        if (createdTo == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), createdTo);
    }
}