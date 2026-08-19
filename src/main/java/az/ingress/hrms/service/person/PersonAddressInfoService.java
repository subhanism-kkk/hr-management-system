package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.criteria.PersonAddressInfoSearchCriteria;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonAddressInfoService {

    PersonAddressInfoResponse create(PersonAddressInfoCreateRequest request);

    PersonAddressInfoResponse update(
            Integer id,
            PersonAddressInfoUpdateRequest request);

    PersonAddressInfoResponse getById(Integer id);

    PageResponse<PersonAddressInfoResponse> getAll(PersonAddressInfoSearchCriteria criteria, Pageable pageable);

    void softDelete(Integer id);

    void restore(Integer id);

    PersonAddressInfoResponse activate(Integer id);

    PersonAddressInfoResponse deactivate(Integer id);
}
