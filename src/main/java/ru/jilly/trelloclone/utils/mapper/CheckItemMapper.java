package ru.jilly.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;
import ru.jilly.trelloclone.dto.checkitem.CheckItemResponse;
import ru.jilly.trelloclone.entity.CheckItem;

@Component
@RequiredArgsConstructor
public class CheckItemMapper {
    private final ModelMapper modelMapper;
    public CheckItem toEntity(CheckItemRequest checkItemDto) {
        return modelMapper.map(checkItemDto, CheckItem.class);
    }
    public CheckItemResponse toDto(CheckItem checkItem) {
        return modelMapper.map(checkItem, CheckItemResponse.class);
    }
    public void mergeDtoIntoEntity(CheckItemRequest checkItemDto, CheckItem checkItem) {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(checkItemDto, checkItem);
    }
}
