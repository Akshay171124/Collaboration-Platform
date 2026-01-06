package com.akshay.notionlite.workspaces.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "workspace_members")
public class WorkspaceMemberEntity {

    @EmbeddedId
    private WorkspaceMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt = Instant.now();

    public WorkspaceMemberId getId() {
        return id;
    }

    public void setId(WorkspaceMemberId id) {
        this.id = id;
    }

    public WorkspaceRole getRole() {
        return role;
    }

    public void setRole(WorkspaceRole role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
