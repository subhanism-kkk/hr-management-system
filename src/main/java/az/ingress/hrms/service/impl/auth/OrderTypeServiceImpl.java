package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.entity.lookup.OrderType;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.lookup.orderType.OrderTypeLogService;
import az.ingress.hrms.mapper.OrderTypeMapper;
import az.ingress.hrms.repository.OrderTypeRepository;
import az.ingress.hrms.service.auth.OrderTypeService;
import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderTypeServiceImpl implements OrderTypeService {

    private final OrderTypeRepository repository;
    private final OrderTypeMapper mapper;
    private final OrderTypeLogService orderTypeLogService;

    @Override
    @Transactional
    public OrderTypeResponse create(OrderTypeRequest request) {
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Order type with name '" + request.getName() + "' already exists."
            );
        }
        OrderType orderType = mapper.toEntity(request);

        repository.save(orderType);

        orderTypeLogService.log(
                orderType,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(orderType);
    }

    @Override
    @Transactional
    public OrderTypeResponse update(Integer id, OrderTypeRequest request) {

        OrderType orderType = fetchOrdertype(id);

        if (!orderType.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Order type with name '" + request.getName() + "' already exists."
            );
        }

        orderTypeLogService.log(
                orderType,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        mapper.updateEntity(orderType, request);

        repository.save(orderType);

        return mapper.toResponse(orderType);
    }

    @Override
    public OrderTypeResponse getById(Integer id) {
        OrderType orderType = fetchOrdertype(id);
        return mapper.toResponse(orderType);
    }

    @Override
    public PageResponse<OrderTypeResponse> getAll(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(
                        pageNo,
                        pageSize,
                        Sort.by("id").ascending()
                );

        Page<OrderType> page =
                repository.findAll(pageable);

        return PaginationUtils.toPageResponse(
                page,
                mapper::toResponse
        );

    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        OrderType orderType = fetchOrdertype(id);

        orderTypeLogService.log(
                orderType,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()
        );

        orderType.setIsDeleted(true);
        orderType.setDeletedAt(LocalDateTime.now());

        orderType.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(orderType);

    }


    @Override
    @Transactional
    public void restore(Integer id) {

        OrderType orderType = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order type not found."));

        if (!Boolean.TRUE.equals(orderType.getIsDeleted())) {
            throw new IllegalStateException("OrderType is not deleted.");
        }

        orderTypeLogService.log(
                orderType,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        orderType.setIsDeleted(false);
        orderType.setDeletedAt(null);
        orderType.setDeletedBy(null);

        repository.save(orderType);
    }


    private OrderType fetchOrdertype(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "OrderType is deleted");
                            });

                    throw new ResourceNotFoundException("OrderType not found.");
                });
    }
}
