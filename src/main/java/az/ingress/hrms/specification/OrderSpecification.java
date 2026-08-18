package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.OrderSearchCriteria;
import az.ingress.hrms.entity.order.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(cb.lower(root.get("orderNumber")), pattern);
    }

    public static Specification<Order> hasOrderTypeId(Long orderTypeId) {
        if (orderTypeId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("orderType").get("id"), orderTypeId);
    }

    public static Specification<Order> hasOrderTypeCode(String orderTypeCode) {
        if (!StringUtils.hasText(orderTypeCode)) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        cb.upper(root.get("orderType").get("code")),
                        orderTypeCode.trim().toUpperCase()
                );
    }

    public static Specification<Order> orderDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("orderDate"), from);
    }

    public static Specification<Order> orderDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("orderDate"), to);
    }

    public static Specification<Order> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        cb.upper(root.get("status").get("code")),
                        statusCode.trim().toUpperCase()
                );
    }

    public static Specification<Order> build(OrderSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getKeyword()))
                .and(hasOrderTypeId(criteria.getOrderTypeId()))
                .and(hasOrderTypeCode(criteria.getOrderTypeCode()))
                .and(orderDateFrom(criteria.getOrderDateFrom()))
                .and(orderDateTo(criteria.getOrderDateTo()))
                .and(hasStatusCode(criteria.getStatusCode()));
    }
}