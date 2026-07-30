package az.ingress.hrms.service.person;

import az.ingress.hrms.dto.personPhoto.PersonPhotoCreateRequest;
import az.ingress.hrms.dto.personPhoto.PersonPhotoResponse;
import az.ingress.hrms.dto.personPhoto.PersonPhotoUpdateRequest;

import java.util.List;

public interface PersonPhotoService {

    PersonPhotoResponse create(PersonPhotoCreateRequest request);

    PersonPhotoResponse update(Integer id, PersonPhotoUpdateRequest request);

    PersonPhotoResponse getById(Integer id);

    List<PersonPhotoResponse> getAll();

    List<PersonPhotoResponse> getAllByPerson(Integer personId);

    PersonPhotoResponse getMainPhoto(Integer personId);

    void setMainPhoto(Integer photoId);

    void softDelete(Integer id);

    void restore(Integer id);


    PersonPhotoResponse activate(Integer id);

    PersonPhotoResponse deactivate(Integer id);
}
