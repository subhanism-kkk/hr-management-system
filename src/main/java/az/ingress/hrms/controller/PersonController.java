package az.ingress.hrms.controller;

import az.ingress.hrms.service.person.PersonService;
import az.ingress.hrms.dto.person.PersonRequest;
import az.ingress.hrms.dto.person.PersonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/person")
public class PersonController {

    private final PersonService service;

    @PostMapping
    public PersonResponse create(
            @Valid @RequestBody PersonRequest request
    ) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public PersonResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody PersonRequest request) {

        return service.update(id, request);
    }


    @GetMapping("/{id}")
    public PersonResponse getById(
            @PathVariable Integer id
    ) {
        return service.getById(id);
    }

    @GetMapping
    public List<PersonResponse> getAll() {
        return service.getAll();
    }


    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Integer id) {
        service.softDelete(id);
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public void restore(@PathVariable Integer id) {
        service.restore(id);
    }

}
