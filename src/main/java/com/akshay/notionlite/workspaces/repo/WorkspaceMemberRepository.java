package com.akshay.notionlite.workspaces.repo;


import com.akshay.notionlite.workspaces.model.WorkspaceMemberEntity;
import com.akshay.notionlite.workspaces.model.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMemberEntity, WorkspaceMemberId> {
    Optional<WorkspaceMemberEntity> findByIdWorkspaceIdAndIdUserId(UUID workspaceId, UUID userId);
    List<WorkspaceMemberEntity> findByIdWorkspaceId(UUID workspaceId);
    List<WorkspaceMemberEntity> findByIdUserId(UUID userId);
}
