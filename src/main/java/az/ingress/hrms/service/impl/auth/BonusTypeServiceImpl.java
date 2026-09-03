package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.dto.bonusType.BonusTypeRequest;
import az.ingress.hrms.dto.bonusType.BonusTypeResponse;
import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.BonusTypeSearchCriteria;
import az.ingress.hrms.entity.lookup.BonusType;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.helper.StatusHelper;
import az.ingress.hrms.log.LogAction;
import az.ingress.hrms.log.lookup.bonusType.BonusTypeLogService;
import az.ingress.hrms.mapper.BonusTypeMapper;
import az.ingress.hrms.repository.BonusTypeRepository;
import az.ingress.hrms.service.auth.BonusTypeService;
import az.ingress.hrms.specification.BonusTypeSpecification;
import az.ingress.hrms.util.PaginationUtils;
import az.ingress.hrms.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BonusTypeServiceImpl implements BonusTypeService {

    private final BonusTypeRepository repository;
    private final BonusTypeMapper mapper;
    private final BonusTypeLogService bonusTypeLogService;
    private final StatusHelper statusHelper;

    @Override
    @Transactional
    public BonusTypeResponse create(BonusTypeRequest request) {
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Bonus type with name '" + request.getName() + "' already exists."
            );
        }
        BonusType bonusType = mapper.toEntity(request);

        repository.save(bonusType);

        bonusTypeLogService.log(
                bonusType,
                LogAction.POST,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(bonusType);
    }

    @Override
    @Transactional
    public BonusTypeResponse update(Integer id, BonusTypeRequest request) {

        BonusType bonusType = fetchBonusType(id);

        if (!bonusType.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Bonus type with name '" + request.getName() + "' already exists."
            );
        }

        mapper.updateEntity(bonusType, request);
        BonusType updatedBonusType = repository.save(bonusType);

        bonusTypeLogService.log(
                updatedBonusType,
                LogAction.PUT,
                SecurityUtils.getCurrentUsername()
        );

        return mapper.toResponse(updatedBonusType);
    }

    @Override
    public BonusTypeResponse getById(Integer id) {
        BonusType bonusType = fetchBonusType(id);
        return mapper.toResponse(bonusType);
    }

    @Override
    public PageResponse<BonusTypeResponse> getAll(BonusTypeSearchCriteria criteria, Pageable pageable) {
        Specification<BonusType> specification = BonusTypeSpecification.build(criteria);
        Page<BonusType> page = repository.findAll(specification, pageable);

        return PaginationUtils.toPageResponse(page, mapper::toResponse);
    }

    @Override
    public List<BonusTypeResponse> getActiveOptions() {
        return repository.findAllActive()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {

        BonusType bonusType = fetchBonusType(id);

        bonusTypeLogService.log(
                bonusType,
                LogAction.DELETE,
                SecurityUtils.getCurrentUsername()
        );

        bonusType.setIsDeleted(true);
        bonusType.setDeletedAt(LocalDateTime.now());
        bonusType.setDeletedBy(SecurityUtils.getCurrentUsername());

        repository.save(bonusType);
    }

    @Override
    @Transactional
    public void restore(Integer id) {

        BonusType bonusType = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bonus type not found."));

        if (!Boolean.TRUE.equals(bonusType.getIsDeleted())) {
            throw new IllegalStateException("BonusType is not deleted.");
        }

        bonusTypeLogService.log(
                bonusType,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        bonusType.setIsDeleted(false);
        bonusType.setDeletedAt(null);
        bonusType.setDeletedBy(null);

        repository.save(bonusType);
    }

    @Override
    @Transactional
    public void activate(Integer id) {
        BonusType bonusType = fetchBonusType(id);

        bonusTypeLogService.log(
                bonusType,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        bonusType.setStatus(statusHelper.getActive());

        repository.save(bonusType);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        BonusType bonusType = fetchBonusType(id);

        bonusTypeLogService.log(
                bonusType,
                LogAction.PATCH,
                SecurityUtils.getCurrentUsername()
        );

        bonusType.setStatus(statusHelper.getInactive());

        repository.save(bonusType);
    }

    private BonusType fetchBonusType(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "BonusType is deleted");
                            });

                    throw new ResourceNotFoundException("BonusType not found.");
                });
    }
}