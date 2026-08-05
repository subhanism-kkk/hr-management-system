package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoCreateRequest;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoResponse;
import az.ingress.hrms.dto.personPersonalInfo.PersonPersonalInfoUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonPersonalInfoService {

    public PersonPersonalInfoResponse create (PersonPersonalInfoCreateRequest request);

    PersonPersonalInfoResponse update(
            Integer id,
            PersonPersonalInfoUpdateRequest request
    );

    PersonPersonalInfoResponse getById(Integer id);

    Page<PersonPersonalInfoResponse> getAll(int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    PersonPersonalInfoResponse activate(Integer id);

    PersonPersonalInfoResponse deactivate(Integer id);
}
