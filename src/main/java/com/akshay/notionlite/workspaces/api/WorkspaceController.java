package com.akshay.notionlite.workspaces.api;

import com.akshay.notionlite.security.SecurityUtil;
import com.akshay.notionlite.workspaces.model.WorkspaceEntity;
import com.akshay.notionlite.workspaces.model.WorkspaceRole;
import com.akshay.notionlite.workspaces.service.WorkspaceAccessService;
import com.akshay.notionlite.workspaces.service.WorkspaceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.akshay.notionlite.workspaces.api.WorkspaceDtos.*;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceAccessService access;

    public WorkspaceController(WorkspaceService workspaceService, WorkspaceAccessService access) {
        this.workspaceService = workspaceService;
        this.access = access;
    }

    @PostMapping
    public WorkspaceRes create(@RequestBody CreateWorkspaceReq req) {
        UUID me = SecurityUtil.currentUserId();
        WorkspaceEntity ws = workspaceService.createWorkspace(me, req.name());
        return toRes(ws);
    }

    @GetMapping
    public List<WorkspaceRes> listMine() {
        UUID me = SecurityUtil.currentUserId();
        return workspaceService.listUserWorkspaces(me).stream().map(WorkspaceController::toRes).toList();
    }

    @GetMapping("/{workspaceId}")
    public WorkspaceRes get(@PathVariable UUID workspaceId) {
        UUID me = SecurityUtil.currentUserId();
        access.requireMemberOr404(workspaceId, me);
        return toRes(workspaceService.getWorkspace(workspaceId));
    }

    @PatchMapping("/{workspaceId}")
    public WorkspaceRes update(@PathVariable UUID workspaceId, @RequestBody UpdateWorkspaceReq req) {
        UUID me = SecurityUtil.currentUserId();
        access.requireAtLeast(workspaceId, me, WorkspaceRole.ADMIN);
        return toRes(workspaceService.updateWorkspace(workspaceId, req.name()));
    }

    private static WorkspaceRes toRes(WorkspaceEntity ws) {
        return new WorkspaceRes(ws.getId(), ws.getName(), ws.getCreatedBy(), ws.getCreatedAt().toString());
    }
}
