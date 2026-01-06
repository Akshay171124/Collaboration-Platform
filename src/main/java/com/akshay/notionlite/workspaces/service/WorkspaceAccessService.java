package com.akshay.notionlite.workspaces.service;

import com.akshay.notionlite.common.ApiException;
import com.akshay.notionlite.workspaces.model.WorkspaceMemberEntity;
import com.akshay.notionlite.workspaces.model.WorkspaceRole;
import com.akshay.notionlite.workspaces.repo.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceAccessService {

    private final WorkspaceMemberRepository memberRepo;

    public WorkspaceAccessService(WorkspaceMemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    public WorkspaceMemberEntity requireMember(UUID workspaceId, UUID userId) {
        return memberRepo.findByIdWorkspaceIdAndIdUserId(workspaceId, userId)
                .orElseThrow(() -> ApiException.forbidden("Not a workspace member"));
    }

    public WorkspaceMemberEntity requireAtLeast(UUID workspaceId, UUID userId, WorkspaceRole minRole) {
        WorkspaceMemberEntity m = requireMember(workspaceId, userId);
        if (!m.getRole().atLeast(minRole)) {
            throw ApiException.forbidden("Insufficient role: requires " + minRole);
        }
        return m;
    }

    // Policy: return 404 to hide existence
    public void requireMemberOr404(UUID workspaceId, UUID userId) {
        memberRepo.findByIdWorkspaceIdAndIdUserId(workspaceId, userId)
                .orElseThrow(() -> ApiException.notFound("Workspace not found"));
    }
}
