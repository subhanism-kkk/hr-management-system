package az.ingress.hrms.specification.person;

import az.ingress.hrms.dto.criteria.PersonAddressInfoSearchCriteria;
import az.ingress.hrms.entity.person.PersonAddressInfo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public final class PersonAddressInfoSpecification {

    private PersonAddressInfoSpecification() {
    }

    public static Specification<PersonAddressInfo> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("address")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern)
        );
    }

    public static Specification<PersonAddressInfo> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<PersonAddressInfo> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<PersonAddressInfo> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<PersonAddressInfo> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<PersonAddressInfo> build(PersonAddressInfoSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}