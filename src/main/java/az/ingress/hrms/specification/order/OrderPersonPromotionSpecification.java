package az.ingress.hrms.specification.order;

import az.ingress.hrms.dto.criteria.OrderPersonPromotionSearchCriteria;
import az.ingress.hrms.entity.order.orderPerson.OrderPersonPromotion;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class OrderPersonPromotionSpecification {

    private OrderPersonPromotionSpecification() {
    }

    public static Specification<OrderPersonPromotion> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String pattern = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("order").get("orderNumber")), pattern),
                cb.like(cb.lower(root.get("person").get("firstName")), pattern),
                cb.like(cb.lower(root.get("person").get("lastName")), pattern),
                cb.like(cb.lower(root.get("oldPosition").get("name")), pattern),
                cb.like(cb.lower(root.get("newPosition").get("name")), pattern)
        );
    }

    public static Specification<OrderPersonPromotion> hasOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<OrderPersonPromotion> hasPersonId(Integer personId) {
        if (personId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("person").get("id"), personId);
    }

    public static Specification<OrderPersonPromotion> hasOldPositionId(Long oldPositionId) {
        if (oldPositionId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("oldPosition").get("id"), oldPositionId);
    }

    public static Specification<OrderPersonPromotion> hasOldPositionName(String oldPositionName) {
        if (!StringUtils.hasText(oldPositionName)) {
            return null;
        }

        String pattern = "%" + oldPositionName.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("oldPosition").get("name")), pattern);
    }

    public static Specification<OrderPersonPromotion> hasNewPositionId(Long newPositionId) {
        if (newPositionId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("newPosition").get("id"), newPositionId);
    }

    public static Specification<OrderPersonPromotion> hasNewPositionName(String newPositionName) {
        if (!StringUtils.hasText(newPositionName)) {
            return null;
        }

        String pattern = "%" + newPositionName.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("newPosition").get("name")), pattern);
    }

    public static Specification<OrderPersonPromotion> effectiveDateFrom(LocalDate from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("effectiveDate"), from);
    }

    public static Specification<OrderPersonPromotion> effectiveDateTo(LocalDate to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("effectiveDate"), to);
    }

    public static Specification<OrderPersonPromotion> hasStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }

        return (root, query, cb) -> cb.equal(
                cb.upper(root.get("status").get("code")),
                statusCode.trim().toUpperCase()
        );
    }

    public static Specification<OrderPersonPromotion> createdFrom(LocalDateTime from) {
        if (from == null) {
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrderPersonPromotion> createdTo(LocalDateTime to) {
        if (to == null) {
            return null;
        }

        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<OrderPersonPromotion> build(OrderPersonPromotionSearchCriteria criteria) {
        if (criteria == null) {
            return Specification.where(null);
        }

        return Specification.where(search(criteria.getSearch()))
                .and(hasOrderId(criteria.getOrderId()))
                .and(hasPersonId(criteria.getPersonId()))
                .and(hasOldPositionId(criteria.getOldPositionId()))
                .and(hasOldPositionName(criteria.getOldPositionName()))
                .and(hasNewPositionId(criteria.getNewPositionId()))
                .and(hasNewPositionName(criteria.getNewPositionName()))
                .and(effectiveDateFrom(criteria.getEffectiveDateFrom()))
                .and(effectiveDateTo(criteria.getEffectiveDateTo()))
                .and(hasStatusCode(criteria.getStatus()))
                .and(createdFrom(criteria.getCreatedFrom()))
                .and(createdTo(criteria.getCreatedTo()));
    }
}