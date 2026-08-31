package az.ingress.hrms.specification.order;

import az.ingress.hrms.dto.criteria.OrderPersonBonusSearchCriteria;
import az.ingress.hrms.enums.BonusCalculationType;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonBonus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public final class OrderPersonBonusSpecification {

    private OrderPersonBonusSpecification() {
    }

    public static Specification<OrderPersonBonus> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("reason")), pattern),
                cb.like(cb.lower(root.get("bonusType").get("name")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern)
        );
    }

    public static Specification<OrderPersonBonus> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<OrderPersonBonus> hasOrderId(Integer orderId) {
        if (orderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderPersonBonus> hasBonusTypeId(Long bonusTypeId) {
        if (bonusTypeId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("bonusType").get("id"), bonusTypeId);
    }

    public static Specification<OrderPersonBonus> hasCalculationType(BonusCalculationType calculationType) {
        if (calculationType == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("calculationType"), calculationType);
    }

    public static Specification<OrderPersonBonus> startDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), from);
    }

    public static Specification<OrderPersonBonus> startDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), to);
    }

    public static Specification<OrderPersonBonus> build(OrderPersonBonusSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasOrderId(criteria.getOrderId()))
                .and(hasBonusTypeId(criteria.getBonusTypeId()))
                .and(hasCalculationType(criteria.getCalculationType()))
                .and(startDateFrom(criteria.getStartDateFrom()))
                .and(startDateTo(criteria.getStartDateTo()));
    }
}