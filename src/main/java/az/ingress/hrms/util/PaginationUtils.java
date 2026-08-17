package az.ingress.hrms.util;

import az.ingress.hrms.dto.common.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static <E, R> PageResponse<R> toPageResponse(
            Page<E> page,
            Function<E, R> mapper
    ) {

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(mapper)
                        .toList(),

                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}