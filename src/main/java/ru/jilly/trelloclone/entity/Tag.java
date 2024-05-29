package ru.jilly.trelloclone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.jilly.trelloclone.entity.id.TaskTagKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_tag")
public class Tag {
    @EmbeddedId
    private TaskTagKey id;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
}
