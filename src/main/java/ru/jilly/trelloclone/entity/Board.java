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
@Table(name = "board")
@NamedEntityGraph(
        name = "board-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("creator"),
                @NamedAttributeNode("members")
        }
)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_board",
            joinColumns = {@JoinColumn(name = "board_id")},
            inverseJoinColumns = {@JoinColumn(name = "member_id")}
    )
    private Set<User> members;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.ALL)
    private Set<Task> tasks;
}
