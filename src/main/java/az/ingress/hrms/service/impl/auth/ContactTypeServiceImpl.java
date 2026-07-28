package az.ingress.hrms.service.impl.auth;

import az.ingress.hrms.entity.lookup.ContactType;
import az.ingress.hrms.entity.order.Order;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.mapper.ContactTypeMapper;
import az.ingress.hrms.repository.ContactTypeRepository;
import az.ingress.hrms.service.auth.ContactTypeService;
import az.ingress.hrms.dto.contactType.ContactTypeRequest;
import az.ingress.hrms.dto.contactType.ContactTypeResponse;
import az.ingress.hrms.exception.ResourceAlreadyExistsException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactTypeServiceImpl implements ContactTypeService {

    private final ContactTypeRepository repository;
    private final ContactTypeMapper mapper;

    @Override
    @Transactional
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
    @Transactional
    public ContactTypeResponse update(Integer id, ContactTypeRequest request) {

        ContactType contactType = fetchContactType(id);

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

        ContactType contactType = fetchContactType(id);

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
    @Transactional
    public void softDelete(Integer id) {

        ContactType contactType = fetchContactType(id);

        contactType.setIsDeleted(true);
        contactType.setDeletedAt(LocalDateTime.now());
        contactType.setDeletedBy("SYSTEM");

        repository.save(contactType);

    }

    @Override
    @Transactional
    public void restore(Integer id) {

         ContactType contactType = repository.findByIdWithDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact type not found."));

        if (!Boolean.TRUE.equals(contactType.getIsDeleted())) {
            throw new IllegalStateException("contactType is not deleted.");
        }

        contactType.setIsDeleted(false);
        contactType.setDeletedAt(null);
        contactType.setDeletedBy(null);

        repository.save(contactType);
    }


    private ContactType fetchContactType(Integer id) {
        return repository.findById(id)
                .orElseGet(() -> {
                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "ContactType is deleted");
                            });

                    throw new ResourceNotFoundException("ContactType not found.");
                });
    }
}
