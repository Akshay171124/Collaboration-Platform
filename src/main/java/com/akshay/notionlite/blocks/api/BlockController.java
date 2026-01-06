package com.akshay.notionlite.blocks.api;

import com.akshay.notionlite.blocks.model.BlockEntity;
import com.akshay.notionlite.blocks.service.BlockService;
import com.akshay.notionlite.pages.model.PageEntity;
import com.akshay.notionlite.security.SecurityUtil;
import com.akshay.notionlite.workspaces.model.WorkspaceRole;
import com.akshay.notionlite.workspaces.service.WorkspaceAccessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.akshay.notionlite.blocks.api.BlockDtos.*;

@RestController
public class BlockController {

    private final BlockService blockService;
    private final WorkspaceAccessService access;

    public BlockController(BlockService blockService, WorkspaceAccessService access) {
        this.blockService = blockService;
        this.access = access;
    }

    @GetMapping("/api/pages/{pageId}/blocks")
    public List<BlockRes> list(@PathVariable UUID pageId) {
        UUID me = SecurityUtil.currentUserId();
        PageEntity page = blockService.getPage(pageId);

        access.requireMemberOr404(page.getWorkspaceId(), me);

        return blockService.list(pageId).stream().map(BlockController::toRes).toList();
    }

    @PostMapping("/api/pages/{pageId}/blocks")
    public BlockRes create(@PathVariable UUID pageId, @RequestBody CreateBlockReq req) {
        UUID me = SecurityUtil.currentUserId();
        PageEntity page = blockService.getPage(pageId);

        access.requireAtLeast(page.getWorkspaceId(), me, WorkspaceRole.EDITOR);

        BlockEntity b = blockService.create(pageId, me, req.type(), req.content(), req.afterBlockId(), req.beforeBlockId());
        return toRes(b);
    }

    @PatchMapping("/api/blocks/{blockId}")
    public BlockRes update(@PathVariable UUID blockId, @RequestBody UpdateBlockReq req) {
        UUID me = SecurityUtil.currentUserId();

        // 1) fetch block (no mutation)
        BlockEntity block = blockService.getBlock(blockId);

        // 2) fetch page (to get workspaceId)
        PageEntity page = blockService.getPage(block.getPageId());

        // 3) authorize
        access.requireAtLeast(page.getWorkspaceId(), me, WorkspaceRole.EDITOR);

        // 4) mutate
        BlockEntity updated = blockService.updateContent(blockId, req.content());
        return toRes(updated);
    }


    @PostMapping("/api/blocks/{blockId}/move")
    public BlockRes move(@PathVariable UUID blockId, @RequestBody MoveBlockReq req) {
        UUID me = SecurityUtil.currentUserId();

        // 1) fetch block
        BlockEntity block = blockService.getBlock(blockId);

        // 2) fetch page
        PageEntity page = blockService.getPage(block.getPageId());

        // 3) authorize
        access.requireAtLeast(page.getWorkspaceId(), me, WorkspaceRole.EDITOR);

        // 4) mutate
        BlockEntity moved = blockService.move(blockId, req.afterBlockId(), req.beforeBlockId());
        return toRes(moved);
    }


    @DeleteMapping("/api/blocks/{blockId}")
    public void delete(@PathVariable UUID blockId) {
        UUID me = SecurityUtil.currentUserId();

        // 1) fetch block
        BlockEntity block = blockService.getBlock(blockId);

        // 2) fetch page
        PageEntity page = blockService.getPage(block.getPageId());

        // 3) authorize
        access.requireAtLeast(page.getWorkspaceId(), me, WorkspaceRole.EDITOR);

        // 4) mutate
        blockService.delete(blockId);
    }


    private static BlockRes toRes(BlockEntity b) {
        return new BlockRes(
                b.getId(),
                b.getPageId(),
                b.getType(),
                b.getPosition().toPlainString(),
                b.getContent(),
                b.getCreatedAt().toString(),
                b.getUpdatedAt().toString()
        );
    }
}
