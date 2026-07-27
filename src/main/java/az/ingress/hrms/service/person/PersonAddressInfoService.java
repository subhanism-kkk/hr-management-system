package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.PersonAddressInfoResponse;
import az.ingress.hrms.dto.PersonAddressInfoUpdateRequest;

import java.util.List;

public interface PersonAddressInfoService {

    PersonAddressInfoResponse create(PersonAddressInfoCreateRequest request);

    PersonAddressInfoResponse update(
            Integer id,
            PersonAddressInfoUpdateRequest request);

    PersonAddressInfoResponse getById(Integer id);

    List<PersonAddressInfoResponse> getAll();

    List<PersonAddressInfoResponse> getAllByPerson(Integer personId);

    void softDelete(Integer id);

    void restore(Integer id);

}
