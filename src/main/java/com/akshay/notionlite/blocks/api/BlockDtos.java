package com.akshay.notionlite.blocks.api;

import com.akshay.notionlite.blocks.model.BlockType;

import java.util.UUID;

public class BlockDtos {

    public record CreateBlockReq(
            BlockType type,
            String content,
            UUID afterBlockId,
            UUID beforeBlockId
    ) {}

    public record UpdateBlockReq(String content) {}

    public record MoveBlockReq(UUID afterBlockId, UUID beforeBlockId) {}

    public record BlockRes(
            UUID id,
            UUID pageId,
            BlockType type,
            String position,
            String content,
            String createdAt,
            String updatedAt
    ) {}
}
