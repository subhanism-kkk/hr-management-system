package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.OrderTypeSearchCriteria;
import az.ingress.hrms.entity.lookup.OrderType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class OrderTypeSpecification {

    private OrderTypeSpecification() {
    }

    public static Specification<OrderType> search(String keyword) {
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

    public static Specification<OrderType> hasName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }

        String pattern = "%" + name.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern);
    }

    public static Specification<OrderType> hasCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("code")),
                code.trim().toUpperCase()
        );
    }

    public static Specification<OrderType> build(OrderTypeSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasName(criteria.getName()))
                .and(hasCode(criteria.getCode()));
    }
}