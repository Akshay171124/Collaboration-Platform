package com.akshay.notionlite.pages.repo;

import com.akshay.notionlite.pages.model.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PageRepository extends JpaRepository<PageEntity, UUID> {
    List<PageEntity> findByWorkspaceIdOrderByUpdatedAtDesc(UUID workspaceId);
}
