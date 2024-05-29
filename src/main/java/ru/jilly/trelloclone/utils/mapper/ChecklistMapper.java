package ru.jilly.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.jilly.trelloclone.dto.checklist.ChecklistRequest;
import ru.jilly.trelloclone.dto.checklist.ChecklistResponse;
import ru.jilly.trelloclone.dto.checklist.AddChecklistRequest;
import ru.jilly.trelloclone.dto.checkitem.CheckItemResponse;
import ru.jilly.trelloclone.entity.CheckItem;
import ru.jilly.trelloclone.entity.Checklist;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChecklistMapper {
    private final ModelMapper modelMapper;
    private final CheckItemMapper checkItemMapper;

    public Checklist toEntity(AddChecklistRequest checklistDto) {
        Checklist checklist = modelMapper.map(checklistDto, Checklist.class);

        Set<CheckItem> checkItems = checklistDto.getCheckItems().stream()
        .map(checkItemMapper::toEntity)
        .peek(checkItem -> checkItem.setChecklist(checklist))
        .collect(Collectors.toSet());

        checklist.setCheckItems(checkItems);

        return checklist;
    }
    public Checklist toEntity(ChecklistRequest checklistDto) {
        return modelMapper.map(checklistDto, Checklist.class);
    }

    public ChecklistResponse toDto(Checklist checklist) {
        ChecklistResponse checklistDto = modelMapper.map(checklist, ChecklistResponse.class);
        Set<CheckItemResponse> checkItemsDto = checklist.getCheckItems().stream()
                .map(checkItemMapper::toDto)
                .collect(Collectors.toSet());
        checklistDto.setCheckItems(checkItemsDto);

        return checklistDto;
    }
    public void mergeEntityToDto(ChecklistRequest checklistDto, Checklist checklist) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(checklistDto, checklist);
    }
}
