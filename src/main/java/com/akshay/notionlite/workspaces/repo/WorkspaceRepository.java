package com.akshay.notionlite.workspaces.repo;

import com.akshay.notionlite.workspaces.model.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, UUID> {}
