package az.ingress.hrms.service.impl.order;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.OrderSearchCriteria;
import az.ingress.hrms.dto.order.OrderDetailResponse;
import az.ingress.hrms.dto.order.OrderRequest;
import az.ingress.hrms.dto.order.OrderResponse;
import az.ingress.hrms.dto.order.OrderUpdateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentCreateRequest;
import az.ingress.hrms.dto.orderPersonAppointment.OrderPersonAppointmentUpdateRequest;
import az.ingress.hrms.dto.orderPersonBonus.CreateOrderPersonBonusRequest;
import az.ingress.hrms.dto.orderPersonBonus.UpdateOrderPersonBonusRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalCreateRequest;
import az.ingress.hrms.dto.orderPersonDismissal.OrderPersonDismissalUpdateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveCreateRequest;
import az.ingress.hrms.dto.orderPersonLeave.OrderPersonLeaveUpdateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionCreateRequest;
import az.ingress.hrms.dto.orderPersonPromotion.OrderPersonPromotionUpdateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryCreateRequest;
import az.ingress.hrms.dto.orderPersonSalary.OrderPersonSalaryUpdateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferCreateRequest;
import az.ingress.hrms.dto.orderPersonTransfer.OrderPersonTransferUpdateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanCreateRequest;
import az.ingress.hrms.dto.staffingPlan.StaffingPlanUpdateRequest;
import az.ingress.hrms.dto.structure.StructureRequest;
import az.ingress.hrms.entity.lookup.OrderType;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.entity.organization.StaffingPlan;
import az.ingress.hrms.exception.BadRequestException;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.order.order.OrderLogService;
import az.ingress.hrms.mapper.order.OrderMapper;
import az.ingress.hrms.repository.OrderTypeRepository;
import az.ingress.hrms.repository.order.OrderRepository;
import az.ingress.hrms.repository.organization.StaffingPlanRepository;
import az.ingress.hrms.service.generator.OrderNumberGenerator;
import az.ingress.hrms.service.order.*;
import az.ingress.hrms.service.organization.StaffingPlanService;
import az.ingress.hrms.service.organization.StructureService;
import az.ingress.hrms.specification.order.OrderSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderNumberGenerator generator;
    private final OrderTypeRepository orderTypeRepository;
    private final StatusHelper statusHelper;
    private final OrderLogService orderLogService;
    private final StaffingPlanRepository staffingPlanRepository;

    private final ObjectMapper objectMapper;
    private final Validator validator;

    private final OrderPersonAppointmentService appointmentService;
    private final OrderPersonDismissalService dismissalService;
    private final OrderPersonLeaveService leaveService;
    private final OrderPersonPromotionService promotionService;
    private final OrderPersonSalaryService salaryService;
    private final OrderPersonTransferService transferService;
    private final OrderPersonBonusService bonusService;
    private final StaffingPlanService staffingPlanService;
    private final StructureService structureService;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        OrderType orderType = fetchOrderType(request.getOrderTypeId());

        Order order = Order.builder()
                .orderType(orderType)
                .orderNumber(generator.generate(orderType))
                .orderDate(request.getOrderDate())
                .status(statusHelper.getActive())
                .isDeleted(false)
                .build();

        Order savedOrder = repository.save(order);

        switch (orderType.getCode()) {
            case "APT":
                for (JsonNode node : request.getData()) {
                    OrderPersonAppointmentCreateRequest appointmentRequest =
                            parseAndValidate(node, OrderPersonAppointmentCreateRequest.class);
                    appointmentService.create(savedOrder, appointmentRequest);
                }
                break;

            case "DIS":
                for (JsonNode node : request.getData()) {
                    OrderPersonDismissalCreateRequest dismissalRequest =
                            parseAndValidate(node, OrderPersonDismissalCreateRequest.class);
                    dismissalService.create(savedOrder, dismissalRequest);
                }
                break;

            case "LEV":
                for (JsonNode node : request.getData()) {
                    OrderPersonLeaveCreateRequest leaveRequest =
                            parseAndValidate(node, OrderPersonLeaveCreateRequest.class);
                    leaveService.create(savedOrder, leaveRequest);
                }
                break;

            case "PRO":
                for (JsonNode node : request.getData()) {
                    OrderPersonPromotionCreateRequest promotionCreateRequest =
                            parseAndValidate(node, OrderPersonPromotionCreateRequest.class);
                    promotionService.create(savedOrder, promotionCreateRequest);
                }
                break;

            case "SAL":
                for (JsonNode node : request.getData()) {
                    OrderPersonSalaryCreateRequest salaryCreateRequest =
                            parseAndValidate(node, OrderPersonSalaryCreateRequest.class);
                    salaryService.create(savedOrder, salaryCreateRequest);
                }
                break;

            case "TRF":
                for (JsonNode node : request.getData()) {
                    OrderPersonTransferCreateRequest transferCreateRequest =
                            parseAndValidate(node, OrderPersonTransferCreateRequest.class);
                    transferService.create(savedOrder, transferCreateRequest);
                }
                break;

            case "BNS":
                for (JsonNode node : request.getData()) {
                    CreateOrderPersonBonusRequest bonusRequest =
                            parseAndValidate(node, CreateOrderPersonBonusRequest.class);
                    bonusService.create(savedOrder, bonusRequest);
                }
                break;

            case "STF":
                for (JsonNode node : request.getData()) {
                    StaffingPlanCreateRequest staffingPlanCreateRequest =
                            parseAndValidate(node, StaffingPlanCreateRequest.class);
                    staffingPlanService.create(savedOrder, staffingPlanCreateRequest);
                }
                break;

            case "STR":
                for (JsonNode node : request.getData()) {
                    StructureRequest structureRequest =
                            parseAndValidate(node, StructureRequest.class);
                    structureService.create(savedOrder, structureRequest);
                }
                break;

            default:
                throw new BadRequestException("Unsupported order type code: " + orderType.getCode());
        }

        orderLogService.log(
                savedOrder,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse update(Integer id, OrderUpdateRequest request) {
        Order order = fetchOrder(id);

        if (request.getOrderDate() != null) {
            order.setOrderDate(request.getOrderDate());
        }

        switch (order.getOrderType().getCode()) {
            case "APT":
                for (JsonNode node : request.getData()) {
                    OrderPersonAppointmentUpdateRequest appointmentRequest =
                            parseAndValidate(node, OrderPersonAppointmentUpdateRequest.class);
                    appointmentService.update(order, appointmentRequest);
                }
                break;

            case "DIS":
                for (JsonNode node : request.getData()) {
                    OrderPersonDismissalUpdateRequest dismissalRequest =
                            parseAndValidate(node, OrderPersonDismissalUpdateRequest.class);
                    dismissalService.update(order, dismissalRequest);
                }
                break;

            case "LEV":
                for (JsonNode node : request.getData()) {
                    OrderPersonLeaveUpdateRequest leaveUpdateRequest =
                            parseAndValidate(node, OrderPersonLeaveUpdateRequest.class);
                    leaveService.update(order, leaveUpdateRequest);
                }
                break;

            case "PRO":
                for (JsonNode node : request.getData()) {
                    OrderPersonPromotionUpdateRequest promotionUpdateRequest =
                            parseAndValidate(node, OrderPersonPromotionUpdateRequest.class);
                    promotionService.update(order, promotionUpdateRequest);
                }
                break;

            case "SAL":
                for (JsonNode node : request.getData()) {
                    OrderPersonSalaryUpdateRequest salaryUpdateRequest =
                            parseAndValidate(node, OrderPersonSalaryUpdateRequest.class);
                    salaryService.update(order, salaryUpdateRequest);
                }
                break;

            case "TRF":
                for (JsonNode node : request.getData()) {
                    OrderPersonTransferUpdateRequest transferUpdateRequest =
                            parseAndValidate(node, OrderPersonTransferUpdateRequest.class);
                    transferService.update(order, transferUpdateRequest);
                }
                break;

            case "BNS":
                for (JsonNode node : request.getData()) {
                    UpdateOrderPersonBonusRequest bonusUpdateRequest =
                            parseAndValidate(node, UpdateOrderPersonBonusRequest.class);
                    bonusService.update(order, bonusUpdateRequest);
                }
                break;

            case "STF":
                for (JsonNode node : request.getData()) {
                    StaffingPlanUpdateRequest staffingPlanUpdateRequest =
                            parseAndValidate(node, StaffingPlanUpdateRequest.class);
                    staffingPlanService.update(order, staffingPlanUpdateRequest);
                }
                break;

            case "STR":
                for (JsonNode node : request.getData()) {
                    StructureRequest structureRequest =
                            parseAndValidate(node, StructureRequest.class);
                    structureService.update(order, structureRequest);
                }
                break;

            default:
                throw new BadRequestException("Unsupported order type code: " + order.getOrderType().getCode());
        }

        orderLogService.log(
                order,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        Order savedOrder = repository.save(order);

        return mapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getById(Integer id) {
        Order order = fetchOrder(id);
        return mapper.toResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getAll(OrderSearchCriteria criteria, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.build(criteria);
        Page<Order> orderPage = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(orderPage, mapper::toResponse);
    }


    @Override
    public OrderDetailResponse getDetailById(Integer id) {
        Order order = fetchOrder(id);
        return mapToDetailResponse(order);
    }

    @Override
    public PageResponse<OrderDetailResponse> getAllDetails(OrderSearchCriteria criteria, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.build(criteria);
        Page<Order> orderPage = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(orderPage, this::mapToDetailResponse);
    }

    private OrderDetailResponse mapToDetailResponse(Order order) {

        List<JsonNode> detailNodes = switch (order.getOrderType().getCode()) {

            case "APT" ->
                    toJsonNodes(
                            appointmentService.getByOrderId(order.getId())
                    );

            case "DIS" ->
                    toJsonNodes(
                            dismissalService.getByOrderId(order.getId())
                    );

            case "LEV" ->
                    toJsonNodes(
                            leaveService.getByOrderId(order.getId())
                    );

            case "PRO" ->
                    toJsonNodes(
                            promotionService.getByOrderId(order.getId())
                    );

            case "SAL" ->
                    toJsonNodes(
                            salaryService.getByOrderId(order.getId())
                    );

            case "TRF" ->
                    toJsonNodes(
                            transferService.getByOrderId(order.getId())
                    );

            case "BNS" ->
                    toJsonNodes(
                            bonusService.getByOrderId(order.getId())
                    );

            case "STF" ->
                    toJsonNodes(
                            staffingPlanService.getByOrderId(order.getId())
                    );

            case "STR" ->
                    toJsonNodes(
                            structureService.getByOrderId(order.getId())
                    );

            default ->
                    throw new BadRequestException(
                            "Unsupported order type code: "
                                    + order.getOrderType().getCode()
                    );
        };

        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderTypeId(
                        order.getOrderType() != null
                                ? order.getOrderType().getId()
                                : null
                )
                .orderTypeName(
                        order.getOrderType() != null
                                ? order.getOrderType().getName()
                                : null
                )
                .orderTypeCode(
                        order.getOrderType() != null
                                ? order.getOrderType().getCode()
                                : null
                )
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .statusId(
                        order.getStatus() != null
                                ? order.getStatus().getId()
                                : null
                )
                .statusName(
                        order.getStatus() != null
                                ? order.getStatus().getName()
                                : null
                )
                .details(detailNodes)
                .build();
    }

    private List<JsonNode> toJsonNodes(List<?> items) {

        return items.stream()
                .map(item -> objectMapper.<JsonNode>valueToTree(item))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void softDelete(Integer id) {
        Order order = fetchOrder(id);
        String username = SecurityUtils.getCurrentUsername();

        switch (order.getOrderType().getCode()) {
            case "APT":
                appointmentService.softDelete(order);
                break;
            case "DIS":
                dismissalService.softDelete(order);
                break;
            case "LEV":
                leaveService.softDelete(order);
                break;
            case "PRO":
                promotionService.softDelete(order);
                break;
            case "SAL":
                salaryService.softDelete(order);
                break;
            case "TRF":
                transferService.softDelete(order);
                break;
            case "BNS":
                bonusService.softDelete(order);
                break;
            case "STF":
                staffingPlanService.softDelete(order);
                break;
            case "STR":
                structureService.softDelete(order);
                break;
            default:
                throw new BadRequestException("Unsupported order type code: " + order.getOrderType().getCode());
        }

        order.setIsDeleted(true);
        order.setDeletedAt(LocalDateTime.now());
        order.setDeletedBy(username);

        orderLogService.log(
                order,
                LogAction.DELETE,
                username
        );

        repository.save(order);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        Order order = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        if (!Boolean.TRUE.equals(order.getIsDeleted())) {
            throw new IllegalStateException("Order is not deleted.");
        }

        switch (order.getOrderType().getCode()) {
            case "APT":
                appointmentService.restore(order);
                break;
            case "DIS":
                dismissalService.restore(order);
                break;
            case "LEV":
                leaveService.restore(order);
                break;
            case "PRO":
                promotionService.restore(order);
                break;
            case "SAL":
                salaryService.restore(order);
                break;
            case "TRF":
                transferService.restore(order);
                break;
            case "BNS":
                bonusService.restore(order);
                break;
            case "STF":
                staffingPlanService.restore(order);
                break;
            case "STR":
                structureService.restore(order);
                break;
            default:
                throw new BadRequestException("Unsupported order type code: " + order.getOrderType().getCode());
        }

        order.setIsDeleted(false);
        order.setDeletedAt(null);
        order.setDeletedBy(null);

        orderLogService.log(
                order,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        repository.save(order);
    }

    @Override
    @Transactional
    public OrderResponse activate(Integer id) {
        Order order = fetchOrder(id);

        order.setStatus(statusHelper.getActive());

        switch (order.getOrderType().getCode()) {
            case "APT":
                appointmentService.activate(order);
                break;
            case "DIS":
                dismissalService.activate(order);
                break;
            case "LEV":
                leaveService.activate(order);
                break;
            case "PRO":
                promotionService.activate(order);
                break;
            case "SAL":
                salaryService.activate(order);
                break;
            case "TRF":
                transferService.activate(order);
                break;
            case "BNS":
                bonusService.activate(order);
                break;
            case "STF":
                staffingPlanService.activate(order);
                break;
            case "STR":
                structureService.activate(order);
                break;
            default:
                throw new BadRequestException("Unsupported order type code: " + order.getOrderType().getCode());
        }

        orderLogService.log(
                order,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        Order savedOrder = repository.save(order);
        return mapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse deactivate(Integer id) {
        Order order = fetchOrder(id);

        switch (order.getOrderType().getCode()) {
            case "APT":
                appointmentService.deactivate(order);
                break;
            case "DIS":
                dismissalService.deactivate(order);
                break;
            case "LEV":
                leaveService.deactivate(order);
                break;
            case "PRO":
                promotionService.deactivate(order);
                break;
            case "SAL":
                salaryService.deactivate(order);
                break;
            case "TRF":
                transferService.deactivate(order);
                break;
            case "BNS":
                bonusService.deactivate(order);
                break;
            case "STF":
                staffingPlanService.deactivate(order);
                break;
            case "STR":
                structureService.deactivate(order);
                break;
            default:
                throw new BadRequestException("Unsupported order type code: " + order.getOrderType().getCode());
        }

        order.setStatus(statusHelper.getInactive());

        orderLogService.log(
                order,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        Order savedOrder = repository.save(order);
        return mapper.toResponse(savedOrder);
    }


    @Override
    @Transactional
    public OrderResponse close(Integer orderId) {
        Order order = fetchOrder(orderId);

        List<StaffingPlan> plans = staffingPlanRepository.findByOrderIdAndIsDeletedFalse(orderId);

        for (StaffingPlan plan : plans) {
            staffingPlanService.close(plan.getId());
        }

        Order savedOrder = repository.save(order);

        return mapper.toResponse(savedOrder);
    }


    @Override
    @Transactional
    public OrderResponse reopen(Integer orderId) {
        Order order = fetchOrder(orderId);

        List<StaffingPlan> plans = staffingPlanRepository.findByOrderIdAndIsDeletedFalse(orderId);

        for (StaffingPlan plan : plans) {
            staffingPlanService.reopen(plan.getId());
        }

        Order savedOrder = repository.save(order);

        return mapper.toResponse(savedOrder);
    }


    private <T> T parseAndValidate(JsonNode node, Class<T> clazz) {
        T dto;
        try {
            dto = objectMapper.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON structure for " + clazz.getSimpleName() + ": " + e.getMessage());
        }

        Set<ConstraintViolation<T>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining(", "));

            throw new BadRequestException("Validation failed for order items: " + errorMessage);
        }

        return dto;
    }

    private Order fetchOrder(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Order is deleted.");
                            });

                    throw new ResourceNotFoundException("Order not found.");
                });
    }

    private OrderType fetchOrderType(Integer id) {
        return orderTypeRepository.findById(id)
                .orElseGet(() -> {
                    orderTypeRepository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException("Order type is deleted.");
                            });

                    throw new ResourceNotFoundException("Order type not found.");
                });
    }


}