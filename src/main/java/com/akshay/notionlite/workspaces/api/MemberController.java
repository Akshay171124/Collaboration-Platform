package com.akshay.notionlite.workspaces.api;

import com.akshay.notionlite.security.SecurityUtil;
import com.akshay.notionlite.workspaces.model.WorkspaceRole;
import com.akshay.notionlite.workspaces.service.MembershipService;
import com.akshay.notionlite.workspaces.service.WorkspaceAccessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.akshay.notionlite.workspaces.api.MemberDtos.*;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/members")
public class MemberController {

    private final WorkspaceAccessService access;
    private final MembershipService membershipService;

    public MemberController(WorkspaceAccessService access, MembershipService membershipService) {
        this.access = access;
        this.membershipService = membershipService;
    }

    @GetMapping
    public List<MemberRes> list(@PathVariable UUID workspaceId) {
        UUID me = SecurityUtil.currentUserId();
        access.requireMemberOr404(workspaceId, me);

        return membershipService.listMembers(workspaceId).stream()
                .map(m -> new MemberRes(m.getId().getUserId(), m.getRole(), m.getJoinedAt().toString()))
                .toList();
    }

    @PostMapping
    public void add(@PathVariable UUID workspaceId, @RequestBody AddMemberReq req) {
        UUID me = SecurityUtil.currentUserId();
        var actor = access.requireAtLeastOr404(workspaceId, me, WorkspaceRole.ADMIN);

        membershipService.addMember(workspaceId, me, actor.getRole(), req.email(), req.role());
    }

    @PatchMapping("/{userId}")
    public void updateRole(@PathVariable UUID workspaceId, @PathVariable UUID userId, @RequestBody UpdateMemberRoleReq req) {
        UUID me = SecurityUtil.currentUserId();
        var actor = access.requireAtLeastOr404(workspaceId, me, WorkspaceRole.ADMIN);

        membershipService.updateRole(workspaceId, me, actor.getRole(), userId, req.role());
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable UUID workspaceId, @PathVariable UUID userId) {
        UUID me = SecurityUtil.currentUserId();
        var actor = access.requireAtLeastOr404(workspaceId, me, WorkspaceRole.ADMIN);

        membershipService.removeMember(workspaceId, actor.getRole(), userId);
    }
}
