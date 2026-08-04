package az.ingress.hrms.service.generator;

import az.ingress.hrms.entity.lookup.OrderType;
import az.ingress.hrms.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {

    private final OrderRepository repository;

    public String generate(OrderType orderType) {

        Long sequence = repository.getNextOrderSequence();

        return String.format(
                "%s-%d-%06d",
                orderType.getCode(),
                Year.now().getValue(),
                sequence
        );
    }
}