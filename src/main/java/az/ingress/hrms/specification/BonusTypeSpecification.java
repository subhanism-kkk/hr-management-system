package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.BonusTypeSearchCriteria;
import az.ingress.hrms.entity.lookup.BonusType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class BonusTypeSpecification {

    private BonusTypeSpecification() {
    }

    public static Specification<BonusType> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }

    public static Specification<BonusType> hasName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        String pattern = "%" + name.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<BonusType> build(BonusTypeSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasName(criteria.getName()));
    }
}