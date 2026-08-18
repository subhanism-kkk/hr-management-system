package az.ingress.hrms.util;

import az.ingress.hrms.exception.BadRequestException;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Set;

public final class SortUtils {

    private SortUtils() {
    }

    public static Sort buildSort(
            String sortBy,
            String sortDir,
            Set<String> allowedFields,
            String defaultField
    ) {

        String field = defaultField;

        if (StringUtils.hasText(sortBy)) {

            if (!allowedFields.contains(sortBy)) {
                throw new BadRequestException(
                        "Invalid sort field: '" + sortBy
                                + "'. Allowed fields: "
                                + allowedFields
                );
            }

            field = sortBy;
        }

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDir)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return Sort.by(direction, field);
    }
}