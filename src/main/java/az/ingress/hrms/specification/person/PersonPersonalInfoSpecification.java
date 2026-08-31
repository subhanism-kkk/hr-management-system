package az.ingress.hrms.specification.person;

import az.ingress.hrms.dto.criteria.PersonPersonalInfoSearchCriteria;
import az.ingress.hrms.entity.person.PersonPersonalInfo;
import az.ingress.hrms.enums.Gender;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PersonPersonalInfoSpecification {

    private PersonPersonalInfoSpecification() {
    }

    public static Specification<PersonPersonalInfo> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("finCode")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern)
        );
    }

    public static Specification<PersonPersonalInfo> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<PersonPersonalInfo> hasGender(Gender gender) {
        if (gender == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("gender"), gender);
    }

    public static Specification<PersonPersonalInfo> hasFinCode(String finCode) {
        if (!StringUtils.hasText(finCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("finCode")),
                finCode.trim().toUpperCase()
        );
    }

    public static Specification<PersonPersonalInfo> birthDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dateOfBirth"), from);
    }

    public static Specification<PersonPersonalInfo> birthDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dateOfBirth"), to);
    }

    public static Specification<PersonPersonalInfo> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<PersonPersonalInfo> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<PersonPersonalInfo> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<PersonPersonalInfo> build(PersonPersonalInfoSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasGender(criteria.getGender()))
                .and(hasFinCode(criteria.getFinCode()))
                .and(birthDateFrom(criteria.getBirthDateFrom()))
                .and(birthDateTo(criteria.getBirthDateTo()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}