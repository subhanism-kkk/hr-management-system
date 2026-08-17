package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonAddressInfoService {

    PersonAddressInfoResponse create(PersonAddressInfoCreateRequest request);

    PersonAddressInfoResponse update(
            Integer id,
            PersonAddressInfoUpdateRequest request);

    PersonAddressInfoResponse getById(Integer id);

    PageResponse<PersonAddressInfoResponse> getAll(int pageNo, int pageSize);

    PageResponse<PersonAddressInfoResponse> getAllByPerson(Integer personId, int pageNo, int pageSize);

    void softDelete(Integer id);

    void restore(Integer id);

    PersonAddressInfoResponse activate(Integer id);

    PersonAddressInfoResponse deactivate(Integer id);
}
