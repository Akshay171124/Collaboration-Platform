package com.akshay.notionlite.pages.api;

import java.util.UUID;

public class PageDtos {
    public record CreatePageReq(String title) {}
    public record UpdatePageReq(String title) {}
    public record PageRes(UUID id, UUID workspaceId, String title, String createdAt, String updatedAt) {}
}
