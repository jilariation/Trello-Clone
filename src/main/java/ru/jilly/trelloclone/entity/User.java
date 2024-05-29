package ru.jilly.trelloclone.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.jilly.trelloclone.entity.enums.Role;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // Relations
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade = CascadeType.ALL)
    private Set<Board> createdBoards;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "members")
    private Set<Board> memberedBoards;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade = CascadeType.ALL)
    private Set<Task> createdTasks;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "executors")
    private Set<Task> assignedTasks;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator", cascade = CascadeType.ALL)
    private Set<Comment> comments;
}
