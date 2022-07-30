package com.github.paolorotolo.appintro;

public class PermissionObject {
    String[] permission;
    int position;

    public PermissionObject(String[] permission2, int position2) {
        this.permission = permission2;
        this.position = position2;
    }

    public String[] getPermission() {
        return this.permission;
    }

    public int getPosition() {
        return this.position;
    }
}
