package org.xutils.db.sqlite;

public enum ColumnDbType {
    INTEGER("INTEGER"),
    REAL("REAL"),
    TEXT("TEXT"),
    BLOB("BLOB");
    
    private String value;

    private ColumnDbType(String value2) {
        this.value = value2;
    }

    public String toString() {
        return this.value;
    }
}
