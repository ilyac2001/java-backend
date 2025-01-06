package ru.andersenlab.infoservice.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andersenlab.infoservice.controller.OfficeController;
import ru.andersenlab.infoservice.domain.dto.OfficePageDTO;
import ru.andersenlab.infoservice.domain.dto.RequestSearchOfficeDTO;
import ru.andersenlab.infoservice.service.OfficeService;

@RestController
@RequestMapping("api/v1/infoservice/offices")
@RequiredArgsConstructor
public class OfficeControllerImpl implements OfficeController {

    private final OfficeService officeService;

    @Override
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OfficePageDTO> getOffices(@PageableDefault(size = 10) Pageable pageable,
                                                    @Valid @RequestBody(required = false) RequestSearchOfficeDTO searchOfficeDTO) {
        OfficePageDTO result = officeService.getOffices(pageable, searchOfficeDTO);
        return ResponseEntity.ok(result);
    }
}
