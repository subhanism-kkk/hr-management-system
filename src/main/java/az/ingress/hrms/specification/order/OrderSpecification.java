package az.ingress.hrms.specification.order;

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

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("orderNumber")), pattern),
                cb.like(cb.lower(root.get("orderType").get("code")), pattern),
                cb.like(cb.lower(root.get("orderType").get("name")), pattern)
        );
    }

    public static Specification<Order> hasOrderTypeId(Integer orderTypeId) {
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

    public static Specification<Order> hasOrderNumber(String orderNumber) {
        if (!StringUtils.hasText(orderNumber)) {
            return null;
        }

        String pattern = "%" + orderNumber.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(cb.lower(root.get("orderNumber")), pattern);
    }

    public static Specification<Order> build(OrderSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        Specification<Order> spec = Specification.where(null);

        String keyword = criteria.getEffectiveKeyword();
        if (StringUtils.hasText(keyword)) {
            spec = spec.and(search(keyword));
        }

        if (StringUtils.hasText(criteria.getOrderNumber())) {
            spec = spec.and(hasOrderNumber(criteria.getOrderNumber()));
        }

        if (criteria.getOrderTypeId() != null) {
            spec = spec.and(hasOrderTypeId(criteria.getOrderTypeId()));
        }

        String typeCode = criteria.getEffectiveOrderTypeCode();
        if (StringUtils.hasText(typeCode)) {
            spec = spec.and(hasOrderTypeCode(typeCode));
        }

        if (criteria.getOrderDateFrom() != null) {
            spec = spec.and(orderDateFrom(criteria.getOrderDateFrom()));
        }

        if (criteria.getOrderDateTo() != null) {
            spec = spec.and(orderDateTo(criteria.getOrderDateTo()));
        }

        String statusCode = criteria.getEffectiveStatusCode();
        if (StringUtils.hasText(statusCode)) {
            spec = spec.and(hasStatusCode(statusCode));
        }

        return spec;
    }
}