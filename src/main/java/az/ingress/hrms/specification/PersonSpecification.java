package az.ingress.hrms.specification;

import az.ingress.hrms.entity.person.Person;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public final class PersonSpecification {

    private PersonSpecification() {
    }

    public static Specification<Person> search(String keyword) {

        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern =
                "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.or(
                        cb.like(
                                cb.lower(root.get("firstName")),
                                pattern
                        ),
                        cb.like(
                                cb.lower(root.get("lastName")),
                                pattern
                        )
                );
    }

    public static Specification<Person> hasStatusCode(
            String statusCode
    ) {

        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        cb.upper(
                                root.get("status").get("code")
                        ),
                        statusCode.trim().toUpperCase()
                );
    }

    public static Specification<Person> createdFrom(
            LocalDateTime from
    ) {

        if (from == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        from
                );
    }

    public static Specification<Person> createdTo(
            LocalDateTime to
    ) {

        if (to == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        to
                );
    }
}