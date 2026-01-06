package com.akshay.notionlite.blocks.service;

import com.akshay.notionlite.blocks.model.BlockEntity;
import com.akshay.notionlite.blocks.model.BlockType;
import com.akshay.notionlite.blocks.repo.BlockRepository;
import com.akshay.notionlite.common.ApiException;
import com.akshay.notionlite.pages.model.PageEntity;
import com.akshay.notionlite.pages.repo.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BlockService {

    private final BlockRepository blockRepo;
    private final PageRepository pageRepo;
    private final BlockOrderService orderService;

    public BlockService(BlockRepository blockRepo, PageRepository pageRepo, BlockOrderService orderService) {
        this.blockRepo = blockRepo;
        this.pageRepo = pageRepo;
        this.orderService = orderService;
    }

    @Transactional(readOnly = true)
    public PageEntity getPage(UUID pageId) {
        return pageRepo.findById(pageId)
                .orElseThrow(() -> ApiException.notFound("Page not found"));
    }

    @Transactional(readOnly = true)
    public BlockEntity getBlock(UUID blockId) {
        return blockRepo.findById(blockId)
                .orElseThrow(() -> ApiException.notFound("Block not found"));
    }

    @Transactional(readOnly = true)
    public List<BlockEntity> list(UUID pageId) {
        return blockRepo.findByPageIdOrderByPositionAsc(pageId);
    }

    @Transactional
    public BlockEntity create(UUID pageId, UUID userId, BlockType type, String content, UUID afterId, UUID beforeId) {
        if (type == null) throw ApiException.badRequest("type required");
        if (content == null || content.isBlank()) throw ApiException.badRequest("content required");

        // Validate neighbors belong to same page via computeNewPosition
        BigDecimal pos = orderService.computeNewPosition(pageId, afterId, beforeId);

        BlockEntity b = new BlockEntity();
        b.setPageId(pageId);
        b.setType(type);
        b.setContent(content);
        b.setPosition(pos);
        b.setCreatedBy(userId);
        b.setCreatedAt(Instant.now());
        b.setUpdatedAt(Instant.now());

        return blockRepo.save(b);
    }

    @Transactional
    public BlockEntity updateContent(UUID blockId, String content) {
        if (content == null || content.isBlank()) throw ApiException.badRequest("content required");

        // Optional improvement: reuse getBlock() so we don't duplicate findById + notFound
        BlockEntity b = getBlock(blockId);

        b.setContent(content);
        b.setUpdatedAt(Instant.now());
        return blockRepo.save(b);
    }

    @Transactional
    public BlockEntity move(UUID blockId, UUID afterId, UUID beforeId) {
        // Optional improvement: reuse getBlock()
        BlockEntity b = getBlock(blockId);

        BigDecimal newPos = orderService.computeNewPosition(b.getPageId(), afterId, beforeId);
        b.setPosition(newPos);
        b.setUpdatedAt(Instant.now());
        return blockRepo.save(b);
    }

    @Transactional
    public void delete(UUID blockId) {
        // Optional improvement: reuse getBlock()
        BlockEntity b = getBlock(blockId);
        blockRepo.delete(b);
    }
}
