package org.apache.commons.compress.archivers.dump;

class Dirent {
    private final int ino;
    private final String name;
    private final int parentIno;
    private final int type;

    Dirent(int ino2, int parentIno2, int type2, String name2) {
        this.ino = ino2;
        this.parentIno = parentIno2;
        this.type = type2;
        this.name = name2;
    }

    /* access modifiers changed from: package-private */
    public int getIno() {
        return this.ino;
    }

    /* access modifiers changed from: package-private */
    public int getParentIno() {
        return this.parentIno;
    }

    /* access modifiers changed from: package-private */
    public int getType() {
        return this.type;
    }

    /* access modifiers changed from: package-private */
    public String getName() {
        return this.name;
    }

    public String toString() {
        return String.format("[%d]: %s", new Object[]{Integer.valueOf(this.ino), this.name});
    }
}
