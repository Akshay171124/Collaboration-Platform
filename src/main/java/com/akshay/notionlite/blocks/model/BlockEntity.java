package com.akshay.notionlite.blocks.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "blocks")
public class BlockEntity {
    @Id @GeneratedValue
    private UUID id;

    @Column(name = "page_id", nullable = false)
    private UUID pageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockType type;

    @Column(nullable = false, precision = 40, scale = 20)
    private BigDecimal position;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String content; // keep as String for MVP; later map with Jackson JsonNode

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPageId() {
        return pageId;
    }

    public void setPageId(UUID pageId) {
        this.pageId = pageId;
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public BigDecimal getPosition() {
        return position;
    }

    public void setPosition(BigDecimal position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
