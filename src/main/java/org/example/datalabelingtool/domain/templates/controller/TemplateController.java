package org.example.datalabelingtool.domain.templates.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.templates.dto.TemplateResponseDto;
import org.example.datalabelingtool.domain.templates.service.TemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponseDto> getTemplateById(@Valid @PathVariable String id) {
        TemplateResponseDto responseDto = templateService.getTemplateById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
