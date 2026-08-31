package az.ingress.hrms.specification.order;

import az.ingress.hrms.dto.criteria.OrderPersonAppointmentSearchCriteria;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonAppointment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderPersonAppointmentSpecification {

    private OrderPersonAppointmentSpecification() {
    }

    public static Specification<OrderPersonAppointment> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("order").get("orderNumber")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern),
                cb.like(cb.lower(root.get("staffingPlan").get("position").get("name")), pattern)
        );
    }

    public static Specification<OrderPersonAppointment> hasOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderPersonAppointment> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<OrderPersonAppointment> hasStaffingPlanId(Long staffingPlanId) {
        if (staffingPlanId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("staffingPlan").get("id"), staffingPlanId);
    }

    public static Specification<OrderPersonAppointment> hasStaffingPlanName(String staffingPlanName) {
        if (!StringUtils.hasText(staffingPlanName)) {
            return null;
        }

        String pattern = "%" + staffingPlanName.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.like(
                cb.lower(root.get("staffingPlan").get("position").get("name")),
                pattern
        );
    }

    public static Specification<OrderPersonAppointment> hasDismissalOrderId(Long dismissalOrderId) {
        if (dismissalOrderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("dismissalOrder").get("id"), dismissalOrderId);
    }

    public static Specification<OrderPersonAppointment> isClosed(Boolean isClosed) {
        if (isClosed == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("isClosed"), isClosed);
    }

    public static Specification<OrderPersonAppointment> startDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), from);
    }

    public static Specification<OrderPersonAppointment> startDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), to);
    }

    public static Specification<OrderPersonAppointment> endDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("endDate"), from);
    }

    public static Specification<OrderPersonAppointment> endDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), to);
    }

    public static Specification<OrderPersonAppointment> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<OrderPersonAppointment> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrderPersonAppointment> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<OrderPersonAppointment> build(OrderPersonAppointmentSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasOrderId(criteria.getOrderId()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasStaffingPlanId(criteria.getStaffingPlanId()))
                .and(hasStaffingPlanName(criteria.getStaffingPlanName()))
                .and(hasDismissalOrderId(criteria.getDismissalOrderId()))
                .and(isClosed(criteria.getIsClosed()))
                .and(startDateFrom(criteria.getStartDateFrom()))
                .and(startDateTo(criteria.getStartDateTo()))
                .and(endDateFrom(criteria.getEndDateFrom()))
                .and(endDateTo(criteria.getEndDateTo()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}