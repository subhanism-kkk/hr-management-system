package az.ingress.hrms.specification;

import az.ingress.hrms.dto.criteria.OrderPersonLeaveSearchCriteria;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonLeave;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderPersonLeaveSpecification {

    private OrderPersonLeaveSpecification() {
    }

    public static Specification<OrderPersonLeave> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("reason")), pattern),
                cb.like(cb.lower(root.get("order").get("orderNumber")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern),
                cb.like(cb.lower(root.get("leaveType").get("name")), pattern),
                cb.like(cb.lower(root.get("leaveType").get("code")), pattern)
        );
    }

    public static Specification<OrderPersonLeave> hasOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderPersonLeave> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<OrderPersonLeave> hasLeaveTypeId(Integer leaveTypeId) {
        if (leaveTypeId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("leaveType").get("id"), leaveTypeId);
    }

    public static Specification<OrderPersonLeave> hasLeaveTypeCode(String leaveTypeCode) {
        if (!StringUtils.hasText(leaveTypeCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("leaveType").get("code")),
                leaveTypeCode.trim().toUpperCase()
        );
    }

    public static Specification<OrderPersonLeave> startDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), from);
    }

    public static Specification<OrderPersonLeave> startDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), to);
    }

    public static Specification<OrderPersonLeave> endDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), from);
    }

    public static Specification<OrderPersonLeave> endDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), to);
    }

    public static Specification<OrderPersonLeave> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<OrderPersonLeave> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrderPersonLeave> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<OrderPersonLeave> build(OrderPersonLeaveSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasOrderId(criteria.getOrderId()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasLeaveTypeId(criteria.getLeaveTypeId()))
                .and(hasLeaveTypeCode(criteria.getLeaveTypeCode()))
                .and(startDateFrom(criteria.getStartDateFrom()))
                .and(startDateTo(criteria.getStartDateTo()))
                .and(endDateFrom(criteria.getEndDateFrom()))
                .and(endDateTo(criteria.getEndDateTo()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}