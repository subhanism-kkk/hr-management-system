package az.ingress.hrms.service.auth;

import az.ingress.hrms.dto.bonusType.BonusTypeRequest;
import az.ingress.hrms.dto.bonusType.BonusTypeResponse;
import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.BonusTypeSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BonusTypeService {
    BonusTypeResponse create(BonusTypeRequest request);
    BonusTypeResponse update(Integer id, BonusTypeRequest request);
    BonusTypeResponse getById(Integer id);
    PageResponse<BonusTypeResponse> getAll(BonusTypeSearchCriteria criteria, Pageable pageable);
    List<BonusTypeResponse> getActiveOptions();
    void activate(Integer id);
    void deactivate(Integer id);
    void softDelete(Integer id);
    void restore(Integer id);
}