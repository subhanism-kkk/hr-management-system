package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.mapper.ContactTypeMapper;
import az.ingress.hrms.repository.ContactTypeRepository;
import az.ingress.hrms.service.auth.ContactTypeService;
import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactTypeServiceImpl implements ContactTypeService {

    private final ContactTypeRepository repository;
    private final ContactTypeMapper mapper;

    @Override
    public ContactTypeResponse create(ContactTypeRequest request) {
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Contact type with name '" + request.getName() + "' already exists."
            );
        }
        ContactType contactType = mapper.toEntity(request);

        repository.save(contactType);

        return mapper.toResponse(contactType);
    }

    @Override
    public ContactTypeResponse update(Integer id, ContactTypeRequest request) {

        ContactType contactType = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact type with id " + id + " was not found."
                ));

        if (!contactType.getName().equalsIgnoreCase(request.getName())
                && repository.existsByNameIgnoreCase(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Contact type with name '" + request.getName() + "' already exists."
            );
        }

        mapper.updateEntity(contactType, request);

        repository.save(contactType);

        return mapper.toResponse(contactType);

    }

    @Override
    public ContactTypeResponse getById(Integer id) {

        ContactType contactType = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact type with id " + id + " was not found."
                        ));
        return mapper.toResponse(contactType);
    }

    @Override
    public List<ContactTypeResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();

    }

    @Override
    public void softDelete(Integer id) {

        ContactType contactType = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contact type  not found."
                        ));

        contactType.setIsDeleted(true);
        contactType.setDeletedAt(LocalDateTime.now());
        contactType.setDeletedBy("SYSTEM");

        repository.save(contactType);

    }

    @Override
    public void restore(Integer id) {

         ContactType contactType = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact type not found."));

        contactType.setIsDeleted(false);
        contactType.setDeletedAt(null);
        contactType.setDeletedBy(null);

        repository.save(contactType);
    }
}
