package com.akshay.notionlite.workspaces.api;

import java.util.UUID;

public class WorkspaceDtos {

    public record CreateWorkspaceReq(String name) {}
    public record UpdateWorkspaceReq(String name) {}

    public record WorkspaceRes(UUID id, String name, UUID createdBy, String createdAt) {}

}
