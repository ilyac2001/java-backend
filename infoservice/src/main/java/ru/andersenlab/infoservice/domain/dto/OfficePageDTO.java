package ru.andersenlab.infoservice.domain.dto;


import java.util.List;

public record OfficePageDTO(
        List<OfficeOutputDTO> offices,
        int currentPage,
        long totalOffices,
        int totalPages
) {
}
