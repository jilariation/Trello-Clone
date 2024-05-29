package ru.jilly.trelloclone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "checklist")
@NamedEntityGraph(
        name = "checklist-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("checkItems")
        }
)
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "checklist", cascade = CascadeType.ALL)
    private Set<CheckItem> checkItems;
}
