package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.personContactInfo.PersonContactInfoCreateRequest;
import az.ingress.hrms.dto.personContactInfo.PersonContactInfoResponse;

import java.util.List;

public interface PersonContactInfoService {
    PersonContactInfoResponse create(PersonContactInfoCreateRequest request);

    PersonContactInfoResponse update(Integer id, PersonContactInfoCreateRequest request);

    PersonContactInfoResponse getById(Integer id);

    List<PersonContactInfoResponse> getAll();

    List<PersonContactInfoResponse> getAllByPerson(Integer personId);

    void softDelete(Integer id);

    void restore(Integer id);

}
