package ru.andersenlab.infoservice.controller;


import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.andersenlab.infoservice.domain.dto.OfficePageDTO;
import ru.andersenlab.infoservice.domain.dto.RequestSearchOfficeDTO;

public interface OfficeController {

    ResponseEntity<OfficePageDTO> getOffices(Pageable pageable, RequestSearchOfficeDTO search);

}
