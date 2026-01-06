package com.akshay.notionlite.workspaces.model;

public enum WorkspaceRole {
    OWNER(4), ADMIN(3), EDITOR(2), VIEWER(1);
    private final int rank;
    WorkspaceRole(int rank){
        this.rank = rank;
    }
    public boolean atLeast(WorkspaceRole required){
        return this.rank >= required.rank;
    }
}
