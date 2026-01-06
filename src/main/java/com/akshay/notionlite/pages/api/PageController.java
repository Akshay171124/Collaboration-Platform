package com.akshay.notionlite.pages.api;

import com.akshay.notionlite.pages.model.PageEntity;
import com.akshay.notionlite.pages.service.PageService;
import com.akshay.notionlite.security.SecurityUtil;
import com.akshay.notionlite.workspaces.model.WorkspaceRole;
import com.akshay.notionlite.workspaces.service.WorkspaceAccessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.akshay.notionlite.pages.api.PageDtos.*;

@RestController
public class PageController {

    private final PageService pageService;
    private final WorkspaceAccessService access;

    public PageController(PageService pageService, WorkspaceAccessService access) {
        this.pageService = pageService;
        this.access = access;
    }

    @PostMapping("/api/workspaces/{workspaceId}/pages")
    public PageRes create(@PathVariable UUID workspaceId, @RequestBody CreatePageReq req) {
        UUID me = SecurityUtil.currentUserId();
        access.requireAtLeast(workspaceId, me, WorkspaceRole.EDITOR);

        PageEntity p = pageService.create(workspaceId, me, req.title());
        return toRes(p);
    }

    @GetMapping("/api/workspaces/{workspaceId}/pages")
    public List<PageRes> list(@PathVariable UUID workspaceId) {
        UUID me = SecurityUtil.currentUserId();
        access.requireMemberOr404(workspaceId, me);

        return pageService.list(workspaceId).stream().map(PageController::toRes).toList();
    }

    @GetMapping("/api/pages/{pageId}")
    public PageRes get(@PathVariable UUID pageId) {
        UUID me = SecurityUtil.currentUserId();
        PageEntity p = pageService.get(pageId);

        access.requireMemberOr404(p.getWorkspaceId(), me);
        return toRes(p);
    }

    @PatchMapping("/api/pages/{pageId}")
    public PageRes update(@PathVariable UUID pageId, @RequestBody UpdatePageReq req) {
        UUID me = SecurityUtil.currentUserId();
        PageEntity existing = pageService.get(pageId);

        access.requireAtLeast(existing.getWorkspaceId(), me, WorkspaceRole.EDITOR);
        return toRes(pageService.updateTitle(pageId, req.title()));
    }

    @DeleteMapping("/api/pages/{pageId}")
    public void delete(@PathVariable UUID pageId) {
        UUID me = SecurityUtil.currentUserId();
        PageEntity existing = pageService.get(pageId);

        access.requireAtLeast(existing.getWorkspaceId(), me, WorkspaceRole.EDITOR);
        pageService.delete(pageId);
    }

    private static PageRes toRes(PageEntity p) {
        return new PageRes(p.getId(), p.getWorkspaceId(), p.getTitle(), p.getCreatedAt().toString(), p.getUpdatedAt().toString());
    }
}
