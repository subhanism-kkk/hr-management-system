package az.ingress.hrms.specification.order;

import az.ingress.hrms.dto.criteria.OrderPersonDismissalSearchCriteria;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonDismissal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderPersonDismissalSpecification {

    private OrderPersonDismissalSpecification() {
    }

    public static Specification<OrderPersonDismissal> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("description")), pattern),
                cb.like(cb.lower(root.get("order").get("orderNumber")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern)
        );
    }

    public static Specification<OrderPersonDismissal> hasOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderPersonDismissal> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<OrderPersonDismissal> dismissalDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dismissalDate"), from);
    }

    public static Specification<OrderPersonDismissal> dismissalDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("dismissalDate"), to);
    }

    public static Specification<OrderPersonDismissal> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<OrderPersonDismissal> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrderPersonDismissal> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<OrderPersonDismissal> build(OrderPersonDismissalSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasOrderId(criteria.getOrderId()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(dismissalDateFrom(criteria.getDismissalDateFrom()))
                .and(dismissalDateTo(criteria.getDismissalDateTo()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}