package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.PersonContactInfoSearchCriteria;
import az.ingress.hrms.entity.person.PersonContactInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public final class PersonContactInfoSpecification {

    private PersonContactInfoSpecification() {
    }

    public static Specification<PersonContactInfo> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("contactValue")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern)
        );
    }

    public static Specification<PersonContactInfo> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<PersonContactInfo> hasContactTypeId(Integer contactTypeId) {
        if (contactTypeId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("contactType").get("id"), contactTypeId);
    }

    public static Specification<PersonContactInfo> isPrimary(Boolean isPrimary) {
        if (isPrimary == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("isPrimary"), isPrimary);
    }

    public static Specification<PersonContactInfo> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<PersonContactInfo> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<PersonContactInfo> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<PersonContactInfo> build(PersonContactInfoSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasContactTypeId(criteria.getContactTypeId()))
                .and(isPrimary(criteria.getIsPrimary()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}