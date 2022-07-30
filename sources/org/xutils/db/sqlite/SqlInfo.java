package org.xutils.db.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.List;
import org.xutils.common.util.KeyValue;
import org.xutils.db.converter.ColumnConverter;
import org.xutils.db.converter.ColumnConverterFactory;
import org.xutils.db.table.ColumnUtils;

public final class SqlInfo {
    private List<KeyValue> bindArgs;
    private String sql;

    public SqlInfo() {
    }

    public SqlInfo(String sql2) {
        this.sql = sql2;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql2) {
        this.sql = sql2;
    }

    public void addBindArg(KeyValue kv) {
        if (this.bindArgs == null) {
            this.bindArgs = new ArrayList();
        }
        this.bindArgs.add(kv);
    }

    public void addBindArgs(List<KeyValue> bindArgs2) {
        List<KeyValue> list = this.bindArgs;
        if (list == null) {
            this.bindArgs = bindArgs2;
        } else {
            list.addAll(bindArgs2);
        }
    }

    public SQLiteStatement buildStatement(SQLiteDatabase database) {
        SQLiteStatement result = database.compileStatement(this.sql);
        if (this.bindArgs != null) {
            for (int i = 1; i < this.bindArgs.size() + 1; i++) {
                KeyValue kv = this.bindArgs.get(i - 1);
                if (kv.value == null) {
                    result.bindNull(i);
                } else {
                    ColumnConverter converter = ColumnConverterFactory.getColumnConverter(kv.value.getClass());
                    Object value = converter.fieldValue2DbValue(kv.value);
                    int i2 = AnonymousClass1.$SwitchMap$org$xutils$db$sqlite$ColumnDbType[converter.getColumnDbType().ordinal()];
                    if (i2 == 1) {
                        result.bindLong(i, ((Number) value).longValue());
                    } else if (i2 == 2) {
                        result.bindDouble(i, ((Number) value).doubleValue());
                    } else if (i2 == 3) {
                        result.bindString(i, value.toString());
                    } else if (i2 != 4) {
                        result.bindNull(i);
                    } else {
                        result.bindBlob(i, (byte[]) value);
                    }
                }
            }
        }
        return result;
    }

    /* renamed from: org.xutils.db.sqlite.SqlInfo$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$xutils$db$sqlite$ColumnDbType = new int[ColumnDbType.values().length];

        static {
            try {
                $SwitchMap$org$xutils$db$sqlite$ColumnDbType[ColumnDbType.INTEGER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$xutils$db$sqlite$ColumnDbType[ColumnDbType.REAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$xutils$db$sqlite$ColumnDbType[ColumnDbType.TEXT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$xutils$db$sqlite$ColumnDbType[ColumnDbType.BLOB.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public Object[] getBindArgs() {
        Object[] result = null;
        List<KeyValue> list = this.bindArgs;
        if (list != null) {
            result = new Object[list.size()];
            for (int i = 0; i < this.bindArgs.size(); i++) {
                result[i] = ColumnUtils.convert2DbValueIfNeeded(this.bindArgs.get(i).value);
            }
        }
        return result;
    }

    public String[] getBindArgsAsStrArray() {
        String[] result = null;
        List<KeyValue> list = this.bindArgs;
        if (list != null) {
            result = new String[list.size()];
            for (int i = 0; i < this.bindArgs.size(); i++) {
                Object value = ColumnUtils.convert2DbValueIfNeeded(this.bindArgs.get(i).value);
                result[i] = value == null ? null : value.toString();
            }
        }
        return result;
    }
}
