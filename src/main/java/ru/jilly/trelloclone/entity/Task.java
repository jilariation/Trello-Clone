package ru.jilly.trelloclone.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.jilly.trelloclone.entity.enums.Priority;
import ru.jilly.trelloclone.entity.enums.Status;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
@NamedEntityGraph(
        name = "task-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("creator"),
                @NamedAttributeNode("executors"),
                @NamedAttributeNode("tags")
        }
)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "closed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closedAt;
    @Column(name = "comments_count")
    private Long commentsCount;
    @Column(name = "attachments_count")
    private Long attachmentsCount;
    @Column(name = "checkitems_count")
    private Long checkItemsCount;
    @Column(name = "checkitems_checked_count")
    private Long checkItemsCheckedCount;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    private Board board;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "executor_task",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "executor_id")}
    )
    private Set<User> executors;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Checklist> checklists;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Comment> comments;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private Set<TaskAttachment> attachments;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private Set<Tag> tags;
}
