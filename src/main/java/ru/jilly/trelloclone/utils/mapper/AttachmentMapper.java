package ru.jilly.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.jilly.trelloclone.entity.TaskAttachment;
import ru.jilly.trelloclone.dto.attachments.AttachmentResponse;

@Component
@RequiredArgsConstructor
public class AttachmentMapper {
    private final ModelMapper modelMapper;
    public AttachmentResponse toDto(TaskAttachment attachment) {
        return modelMapper.map(attachment, AttachmentResponse.class);
    }
}
