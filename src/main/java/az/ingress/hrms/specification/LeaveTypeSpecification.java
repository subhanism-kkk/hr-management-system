package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.LeaveTypeSearchCriteria;
import az.ingress.hrms.entity.lookup.LeaveType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public final class LeaveTypeSpecification {

    private LeaveTypeSpecification() {
    }

    public static Specification<LeaveType> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("code")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }

    public static Specification<LeaveType> hasCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("code")),
                code.trim().toUpperCase()
        );
    }

    public static Specification<LeaveType> hasName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        String pattern = "%" + name.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<LeaveType> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<LeaveType> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<LeaveType> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<LeaveType> build(LeaveTypeSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasCode(criteria.getCode()))
                .and(hasName(criteria.getName()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}