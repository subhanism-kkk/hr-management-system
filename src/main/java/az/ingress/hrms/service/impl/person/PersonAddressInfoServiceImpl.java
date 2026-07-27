package az.ingress.hrms.service.impl.person;

import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoCreateRequest;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoResponse;
import az.ingress.hrms.dto.personAddressInfo.PersonAddressInfoUpdateRequest;
import az.ingress.hrms.entity.person.Person;
import az.ingress.hrms.entity.person.PersonAddressInfo;
import az.ingress.hrms.exception.DeletedResourceException;
import az.ingress.hrms.exception.DuplicateResourceException;
import az.ingress.hrms.exception.ResourceNotFoundException;
import az.ingress.hrms.mapper.PersonAddressInfoMapper;
import az.ingress.hrms.repository.PersonAddressInfoRepository;
import az.ingress.hrms.repository.PersonRepository;
import az.ingress.hrms.service.person.PersonAddressInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonAddressInfoServiceImpl implements PersonAddressInfoService {


    private final PersonAddressInfoRepository repository;
    private final PersonRepository personRepository;
    private final PersonAddressInfoMapper mapper;


    @Override
    public PersonAddressInfoResponse create(PersonAddressInfoCreateRequest request) {
        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found"
                ));

        String address = request.getAddress().trim();

        if (repository.existsByPersonAndAddressIgnoreCase(person, address)) {
            throw new DuplicateResourceException(
                    "This address already exists for the person."
            );
        }

        PersonAddressInfo entity = mapper.toEntity(request);

        entity.setPerson(person);
        entity.setAddress(address);

        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonAddressInfoResponse update(
            Integer id, PersonAddressInfoUpdateRequest request) {
        PersonAddressInfo entity = repository.findById(id)
                .orElseGet(() -> {

                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Address information is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Address information not found."
                    );
                });

        String address = request.getAddress().trim();

        if (!entity.getAddress().equalsIgnoreCase(address)) {

            if (repository.existsByPersonAndAddressIgnoreCase(
                    entity.getPerson(), address)) {
                throw new DuplicateResourceException(
                        "This address already exists for the person.");
            }
        }
        mapper.updateEntity(entity, request);

        entity.setAddress(address);
        repository.save(entity);

        return mapper.toResponse(entity);
    }

    @Override
    public PersonAddressInfoResponse getById(Integer id) {
        PersonAddressInfo entity = repository.findById(id)
                .orElseGet(() -> {

                    repository.findByIdWithDeleted(id)
                            .ifPresent(e -> {
                                throw new DeletedResourceException(
                                        "Address information is deleted."
                                );
                            });

                    throw new ResourceNotFoundException(
                            "Address information not found."
                    );
                });

        return mapper.toResponse(entity);
    }

    @Override
    public List<PersonAddressInfoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<PersonAddressInfoResponse> getAllByPerson(Integer personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found"
                ));

        return repository.findByPerson(person)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void softDelete(Integer id) {
        PersonAddressInfo entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address information not found."
                        ));

        entity.setIsDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy("SYSTEM");

        repository.save(entity);
    }

    @Override
    public void restore(Integer id) {
        PersonAddressInfo entity = repository.findByIdWithDeleted(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address information not found."
                        ));

        entity.setIsDeleted(false);
        entity.setDeletedAt(null);
        entity.setDeletedBy(null);

        repository.save(entity);
    }
}
