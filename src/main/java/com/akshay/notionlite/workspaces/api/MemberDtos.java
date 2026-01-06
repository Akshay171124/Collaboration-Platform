package com.akshay.notionlite.workspaces.api;

import com.akshay.notionlite.workspaces.model.WorkspaceRole;

import java.util.UUID;

public class MemberDtos {

    public record AddMemberReq(String email, WorkspaceRole role) {}
    public record UpdateMemberRoleReq(WorkspaceRole role) {}

    public record MemberRes(UUID userId, WorkspaceRole role, String joinedAt) {}
}
