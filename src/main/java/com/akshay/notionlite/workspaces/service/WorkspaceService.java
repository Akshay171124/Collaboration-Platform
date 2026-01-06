package com.akshay.notionlite.workspaces.service;

import com.akshay.notionlite.common.ApiException;
import com.akshay.notionlite.workspaces.model.*;
import com.akshay.notionlite.workspaces.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepo;
    private final WorkspaceMemberRepository memberRepo;

    public WorkspaceService(WorkspaceRepository workspaceRepo, WorkspaceMemberRepository memberRepo) {
        this.workspaceRepo = workspaceRepo;
        this.memberRepo = memberRepo;
    }

    @Transactional
    public WorkspaceEntity createWorkspace(UUID creatorId, String name) {
        if (name == null || name.trim().isEmpty()) throw ApiException.badRequest("Workspace name is required");

        WorkspaceEntity ws = new WorkspaceEntity();
        ws.setName(name.trim());
        ws.setCreatedBy(creatorId);
        ws.setCreatedAt(Instant.now());

        ws = workspaceRepo.save(ws);

        WorkspaceMemberEntity member = new WorkspaceMemberEntity();
        WorkspaceMemberId id = new WorkspaceMemberId();
        id.setWorkspaceId(ws.getId());
        id.setUserId(creatorId);
        member.setId(id);
        member.setRole(WorkspaceRole.OWNER);
        member.setJoinedAt(Instant.now());

        memberRepo.save(member);
        return ws;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceEntity> listUserWorkspaces(UUID userId) {
        // simplest: via membership table
        return memberRepo.findByIdUserId(userId).stream()
                .map(m -> workspaceRepo.findById(m.getId().getWorkspaceId())
                        .orElseThrow(() -> ApiException.notFound("Workspace not found")))
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceEntity getWorkspace(UUID workspaceId) {
        return workspaceRepo.findById(workspaceId)
                .orElseThrow(() -> ApiException.notFound("Workspace not found"));
    }

    @Transactional
    public WorkspaceEntity updateWorkspace(UUID workspaceId, String newName) {
        if (newName == null || newName.trim().isEmpty()) throw ApiException.badRequest("Workspace name is required");
        WorkspaceEntity ws = getWorkspace(workspaceId);
        ws.setName(newName.trim());
        return workspaceRepo.save(ws);
    }
}
