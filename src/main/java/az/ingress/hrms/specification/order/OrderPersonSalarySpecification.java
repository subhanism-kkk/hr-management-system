package az.ingress.hrms.specification.order;

import az.ingress.hrms.dto.criteria.OrderPersonSalarySearchCriteria;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonSalary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderPersonSalarySpecification {

    private OrderPersonSalarySpecification() {
    }

    public static Specification<OrderPersonSalary> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("order").get("orderNumber")), pattern),
                cb.like(cb.lower(root.get("staffingPlan").get("position").get("name")), pattern),
                cb.like(cb.lower(root.get("staffingPlan").get("structure").get("name")), pattern)
        );
    }

    public static Specification<OrderPersonSalary> hasOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderPersonSalary> hasStaffingPlanId(Integer staffingPlanId) {
        if (staffingPlanId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("staffingPlan").get("id"), staffingPlanId);
    }

    public static Specification<OrderPersonSalary> effectiveDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("effectiveDate"), from);
    }

    public static Specification<OrderPersonSalary> effectiveDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("effectiveDate"), to);
    }

    public static Specification<OrderPersonSalary> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<OrderPersonSalary> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrderPersonSalary> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<OrderPersonSalary> build(OrderPersonSalarySearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasOrderId(criteria.getOrderId()))
                .and(hasStaffingPlanId(criteria.getStaffingPlanId()))
                .and(effectiveDateFrom(criteria.getEffectiveDateFrom()))
                .and(effectiveDateTo(criteria.getEffectiveDateTo()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}