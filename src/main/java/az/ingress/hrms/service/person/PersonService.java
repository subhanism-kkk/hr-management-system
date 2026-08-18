package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import org.springframework.data.domain.Page;


import java.time.LocalDateTime;
import java.util.List;

public interface PersonService {

    PersonResponse create(PersonRequest request);

    PersonResponse update(Integer id, PersonRequest request);

    PersonResponse getById(Integer id);

    PageResponse<PersonResponse> getAll(
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            String search,
            String status,
            LocalDateTime createdFrom,
            LocalDateTime createdTo
    );
    void softDelete(Integer id);

    void restore(Integer id);

    PersonResponse activate(Integer id);

    PersonResponse deactivate(Integer id);
}
