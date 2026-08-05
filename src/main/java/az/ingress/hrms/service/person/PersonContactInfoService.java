package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonContactInfoService {
    PersonContactInfoResponse create(PersonContactInfoCreateRequest request);

    PersonContactInfoResponse update(Integer id, PersonContactInfoUpdateRequest request);

    PersonContactInfoResponse getById(Integer id);

    Page<PersonContactInfoResponse> getAll(int pageNo, int pageSize);

    Page<PersonContactInfoResponse> getAllByPerson(Integer personId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    PersonContactInfoResponse activate(Integer id);

    PersonContactInfoResponse deactivate(Integer id);

}
