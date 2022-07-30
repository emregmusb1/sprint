package org.xutils.db.sqlite;

import android.text.TextUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xutils.db.table.ColumnUtils;

public class WhereBuilder {
    private final List<String> whereItems = new ArrayList();

    private WhereBuilder() {
    }

    public static WhereBuilder b() {
        return new WhereBuilder();
    }

    public static WhereBuilder b(String columnName, String op, Object value) {
        WhereBuilder result = new WhereBuilder();
        result.appendCondition((String) null, columnName, op, value);
        return result;
    }

    public WhereBuilder and(String columnName, String op, Object value) {
        appendCondition(this.whereItems.size() == 0 ? null : "AND", columnName, op, value);
        return this;
    }

    public WhereBuilder and(WhereBuilder where) {
        String condition = this.whereItems.size() == 0 ? " " : "AND ";
        return expr(condition + "(" + where.toString() + ")");
    }

    public WhereBuilder or(String columnName, String op, Object value) {
        appendCondition(this.whereItems.size() == 0 ? null : "OR", columnName, op, value);
        return this;
    }

    public WhereBuilder or(WhereBuilder where) {
        String condition = this.whereItems.size() == 0 ? " " : "OR ";
        return expr(condition + "(" + where.toString() + ")");
    }

    public WhereBuilder expr(String expr) {
        List<String> list = this.whereItems;
        list.add(" " + expr);
        return this;
    }

    public int getWhereItemSize() {
        return this.whereItems.size();
    }

    public String toString() {
        if (this.whereItems.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String item : this.whereItems) {
            sb.append(item);
        }
        return sb.toString();
    }

    private void appendCondition(String conj, String columnName, String op, Object value) {
        StringBuilder builder = new StringBuilder();
        if (this.whereItems.size() > 0) {
            builder.append(" ");
        }
        if (!TextUtils.isEmpty(conj)) {
            builder.append(conj);
            builder.append(" ");
        }
        builder.append("\"");
        builder.append(columnName);
        builder.append("\"");
        if ("!=".equals(op)) {
            op = "<>";
        } else if ("==".equals(op)) {
            op = "=";
        }
        if (value != null) {
            builder.append(" ");
            builder.append(op);
            builder.append(" ");
            if ("IN".equalsIgnoreCase(op)) {
                Iterable<?> items = null;
                if (value instanceof Iterable) {
                    items = (Iterable) value;
                } else if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    List<Object> arrayList = new ArrayList<>(len);
                    for (int i = 0; i < len; i++) {
                        arrayList.add(Array.get(value, i));
                    }
                    items = arrayList;
                }
                if (items != null) {
                    StringBuilder inSb = new StringBuilder("(");
                    for (Object item : items) {
                        Object itemColValue = ColumnUtils.convert2DbValueIfNeeded(item);
                        if (ColumnUtils.isTextColumnDbType(itemColValue)) {
                            String valueStr = ColumnUtils.convert2SafeExpr(itemColValue);
                            inSb.append("'");
                            inSb.append(valueStr);
                            inSb.append("'");
                        } else {
                            inSb.append(itemColValue);
                        }
                        inSb.append(",");
                    }
                    if (inSb.length() > 1) {
                        inSb.deleteCharAt(inSb.length() - 1);
                    }
                    inSb.append(")");
                    builder.append(inSb.toString());
                } else {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            } else if ("BETWEEN".equalsIgnoreCase(op)) {
                Iterable<?> items2 = null;
                if (value instanceof Iterable) {
                    items2 = (Iterable) value;
                } else if (value.getClass().isArray()) {
                    int len2 = Array.getLength(value);
                    List<Object> arrayList2 = new ArrayList<>(len2);
                    for (int i2 = 0; i2 < len2; i2++) {
                        arrayList2.add(Array.get(value, i2));
                    }
                    items2 = arrayList2;
                }
                if (items2 != null) {
                    Iterator<?> iterator = items2.iterator();
                    if (iterator.hasNext()) {
                        Object start = iterator.next();
                        if (iterator.hasNext()) {
                            Object end = iterator.next();
                            Object startColValue = ColumnUtils.convert2DbValueIfNeeded(start);
                            Object endColValue = ColumnUtils.convert2DbValueIfNeeded(end);
                            if (ColumnUtils.isTextColumnDbType(startColValue)) {
                                String startStr = ColumnUtils.convert2SafeExpr(startColValue);
                                String endStr = ColumnUtils.convert2SafeExpr(endColValue);
                                builder.append("'");
                                builder.append(startStr);
                                builder.append("'");
                                builder.append(" AND ");
                                builder.append("'");
                                builder.append(endStr);
                                builder.append("'");
                            } else {
                                builder.append(startColValue);
                                builder.append(" AND ");
                                builder.append(endColValue);
                            }
                        } else {
                            throw new IllegalArgumentException("value must contains tow items.");
                        }
                    } else {
                        throw new IllegalArgumentException("value must contains tow items.");
                    }
                } else {
                    throw new IllegalArgumentException("value must be an Array or an Iterable.");
                }
            } else {
                Object value2 = ColumnUtils.convert2DbValueIfNeeded(value);
                if (ColumnUtils.isTextColumnDbType(value2)) {
                    String valueStr2 = ColumnUtils.convert2SafeExpr(value2);
                    builder.append("'");
                    builder.append(valueStr2);
                    builder.append("'");
                } else {
                    builder.append(value2);
                }
            }
        } else if ("=".equals(op)) {
            builder.append(" IS NULL");
        } else if ("<>".equals(op)) {
            builder.append(" IS NOT NULL");
        } else {
            builder.append(" ");
            builder.append(op);
            builder.append(" NULL");
        }
        this.whereItems.add(builder.toString());
    }
}
