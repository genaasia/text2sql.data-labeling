package org.example.datalabelingtool.domain.templates.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.datalabelingtool.domain.templates.dto.TemplateResponseDto;
import org.example.datalabelingtool.domain.templates.entity.Template;
import org.example.datalabelingtool.domain.templates.repository.TemplateRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateResponseDto getTemplateById(@Valid String id) {
        Template template = templateRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Template not found")
        );

        return toTemplateResponseDto(template);
    }


    public DataResponseDto getAllTemplates() {
        List<TemplateResponseDto> responseDtoList = templateRepository.findAllOrderByTemplateNoAsc().stream()
                .map(this::toTemplateResponseDto)
                .collect(Collectors.toList());
        return new DataResponseDto(responseDtoList);
    }


    private TemplateResponseDto toTemplateResponseDto(Template template) {
        return TemplateResponseDto.builder()
                .id(template.getId())
                .templateNo(template.getTemplateNo())
                .template(template.getContent())
                .build();
    }
}