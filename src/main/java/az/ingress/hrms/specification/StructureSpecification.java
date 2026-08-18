package az.ingress.hrms.specification;

import az.ingress.hrms.entity.organization.Structure;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class StructureSpecification {
    private StructureSpecification() {

    }

    public static Specification<Structure> search(String keyword) {

        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern =
                "%" + keyword.trim().toLowerCase() + "%";

        return ((root, query, cb) ->
                cb.or(
                        cb.like(
                                cb.lower(root.get("name")),
                                pattern
                        )
                ));
    }

    public static Specification<Structure> hasParentId(Integer parentId) {
        if (parentId == null) {
            return null;
        }

        return ((root, query, cb) ->
                cb.equal(
                        root.get("parentStructure").get("id"),
                        parentId
                ));
    }

    public static Specification<Structure> isClosed(Boolean isClosed) {
        if (isClosed == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                root.get("isClosed"),
                isClosed
        );
    }

    public static Specification<Structure> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(
                        root.get("status").get("code")
                ),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<Structure> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(
                root.get("createdAt"),
                from
        );
    }

    public static Specification<Structure> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(
                root.get("createdAt"),
                to
        );
    }

    public static Specification<Structure> isRoot(Boolean isRoot) {
        if (isRoot == null) {
            return null; // Ignore filter: fetch both roots and non-roots
        }

        return (root, query, cb) -> isRoot
                ? cb.isNull(root.get("parentStructure"))
                : cb.isNotNull(root.get("parentStructure"));
    }

}


