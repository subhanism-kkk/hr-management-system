package az.ingress.hrms.controller;

import az.ingress.hrms.service.order.OrderTypeService;
import az.ingress.hrms.dto.orderType.OrderTypeRequest;
import az.ingress.hrms.dto.orderType.OrderTypeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/order-type")
public class OrderTypeController {

    private final OrderTypeService service;

    @PostMapping
    public OrderTypeResponse create(
            @Valid @RequestBody OrderTypeRequest request
    ) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public OrderTypeResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody OrderTypeRequest request) {

        return service.update(id, request);
    }


    @GetMapping("/{id}")
    public OrderTypeResponse getById(
            @PathVariable Integer id
    ) {
        return service.getById(id);
    }

    @GetMapping
    public List<OrderTypeResponse> getAll() {
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
