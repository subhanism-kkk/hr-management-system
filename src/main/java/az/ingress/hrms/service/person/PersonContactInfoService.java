package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonContactInfoSearchCriteria;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoUpdateRequest;
import org.springframework.data.domain.Pageable;


public interface PersonContactInfoService {
    PersonContactInfoResponse create(PersonContactInfoCreateRequest request);

    PersonContactInfoResponse update(Integer id, PersonContactInfoUpdateRequest request);

    PersonContactInfoResponse getById(Integer id);

    PageResponse<PersonContactInfoResponse> getAll(PersonContactInfoSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    PersonContactInfoResponse activate(Integer id);

    PersonContactInfoResponse deactivate(Integer id);

}
