package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.common.PageResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonPhotoService {

    PersonPhotoResponse create(PersonPhotoCreateRequest request);

    PersonPhotoResponse update(Integer id, PersonPhotoUpdateRequest request);

    PersonPhotoResponse getById(Integer id);

    PageResponse<PersonPhotoResponse> getAll(int pageNo, int pageSize);

    PageResponse<PersonPhotoResponse> getAllByPerson(Integer personId, int pageNo, int pageSize);

    PersonPhotoResponse getMainPhoto(Integer personId);

    void setMainPhoto(Integer photoId);

    void softDelete(Integer id);

    void restore(Integer id);


    PersonPhotoResponse activate(Integer id);

    PersonPhotoResponse deactivate(Integer id);
}
