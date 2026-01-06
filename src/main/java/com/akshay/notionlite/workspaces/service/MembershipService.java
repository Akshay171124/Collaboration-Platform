package com.akshay.notionlite.workspaces.service;

import com.akshay.notionlite.common.ApiException;
import com.akshay.notionlite.users.model.UserEntity;
import com.akshay.notionlite.users.repo.UserRepository;
import com.akshay.notionlite.workspaces.model.*;
import com.akshay.notionlite.workspaces.repo.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MembershipService {

    private final WorkspaceMemberRepository memberRepo;
    private final UserRepository userRepo;

    public MembershipService(WorkspaceMemberRepository memberRepo, UserRepository userRepo) {
        this.memberRepo = memberRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberEntity> listMembers(UUID workspaceId) {
        return memberRepo.findByIdWorkspaceId(workspaceId);
    }

    @Transactional
    public void addMember(UUID workspaceId, UUID actorId, WorkspaceRole actorRole, String email, WorkspaceRole roleToAssign) {
        if (roleToAssign == null) roleToAssign = WorkspaceRole.VIEWER;
        if (email == null || email.isBlank()) throw ApiException.badRequest("email required");

        UserEntity user = userRepo.findByEmail(email.trim())
                .orElseThrow(() -> ApiException.badRequest("No user exists with email: " + email));

        if (roleToAssign == WorkspaceRole.OWNER) {
            throw ApiException.badRequest("Cannot assign OWNER via add-member in MVP");
        }

        // If actor is ADMIN, do not allow assigning ADMIN? (optional)
        // We'll allow ADMIN to assign any non-OWNER.
        var existing = memberRepo.findByIdWorkspaceIdAndIdUserId(workspaceId, user.getId());
        if (existing.isPresent()) throw ApiException.badRequest("User is already a member");

        WorkspaceMemberEntity m = new WorkspaceMemberEntity();
        WorkspaceMemberId id = new WorkspaceMemberId();
        id.setWorkspaceId(workspaceId);
        id.setUserId(user.getId());
        m.setId(id);
        m.setRole(roleToAssign);
        m.setJoinedAt(Instant.now());

        memberRepo.save(m);
    }

    @Transactional
    public void updateRole(UUID workspaceId, UUID actorId, WorkspaceRole actorRole, UUID targetUserId, WorkspaceRole newRole) {
        if (newRole == null) throw ApiException.badRequest("role required");

        WorkspaceMemberEntity target = memberRepo.findByIdWorkspaceIdAndIdUserId(workspaceId, targetUserId)
                .orElseThrow(() -> ApiException.notFound("Member not found"));

        // Admin cannot change OWNER
        if (actorRole == WorkspaceRole.ADMIN && target.getRole() == WorkspaceRole.OWNER) {
            throw ApiException.forbidden("ADMIN cannot modify OWNER");
        }

        // Disallow assigning OWNER in MVP (simplify)
        if (newRole == WorkspaceRole.OWNER) throw ApiException.badRequest("Cannot assign OWNER in MVP");

        target.setRole(newRole);
        memberRepo.save(target);
    }

    @Transactional
    public void removeMember(UUID workspaceId, WorkspaceRole actorRole, UUID targetUserId) {
        WorkspaceMemberEntity target = memberRepo.findByIdWorkspaceIdAndIdUserId(workspaceId, targetUserId)
                .orElseThrow(() -> ApiException.notFound("Member not found"));

        if (target.getRole() == WorkspaceRole.OWNER) {
            throw ApiException.forbidden("Cannot remove OWNER");
        }
        memberRepo.delete(target);
    }
}
