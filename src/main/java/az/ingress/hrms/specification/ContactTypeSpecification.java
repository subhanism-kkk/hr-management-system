package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.ContactTypeSearchCriteria;
import az.ingress.hrms.entity.lookup.ContactType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ContactTypeSpecification {
    private ContactTypeSpecification() {

    }

    public static Specification<ContactType> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }

    public static Specification<ContactType> hasName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        String pattern = "%" + name.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), pattern);
    }


    public static Specification<ContactType> build(ContactTypeSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasName(criteria.getName()));
    }

}
