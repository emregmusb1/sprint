package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;

public class SymbolTable {
    private final int indexMask;
    private final Entry[] symbols;

    public SymbolTable(int tableSize) {
        this.indexMask = tableSize - 1;
        this.symbols = new Entry[tableSize];
        addSymbol("$ref", 0, 4, "$ref".hashCode());
        addSymbol(JSON.DEFAULT_TYPE_KEY, 0, JSON.DEFAULT_TYPE_KEY.length(), JSON.DEFAULT_TYPE_KEY.hashCode());
    }

    public String addSymbol(char[] buffer, int offset, int len, int hash) {
        int bucket = this.indexMask & hash;
        Entry entry = this.symbols[bucket];
        if (entry != null) {
            boolean eq = true;
            if (hash == entry.hashCode && len == entry.chars.length) {
                int i = 0;
                while (true) {
                    if (i >= len) {
                        break;
                    } else if (buffer[offset + i] != entry.chars[i]) {
                        eq = false;
                        break;
                    } else {
                        i++;
                    }
                }
            } else {
                eq = false;
            }
            if (eq) {
                return entry.value;
            }
            return new String(buffer, offset, len);
        }
        String strVal = new String(buffer, offset, len).intern();
        this.symbols[bucket] = new Entry(strVal, hash);
        return strVal;
    }

    public String addSymbol(String buffer, int offset, int len, int hash) {
        String symbol;
        int bucket = this.indexMask & hash;
        Entry entry = this.symbols[bucket];
        if (entry == null) {
            if (len == buffer.length()) {
                symbol = buffer;
            } else {
                symbol = subString(buffer, offset, len);
            }
            String symbol2 = symbol.intern();
            this.symbols[bucket] = new Entry(symbol2, hash);
            return symbol2;
        } else if (hash == entry.hashCode && len == entry.chars.length && buffer.regionMatches(offset, entry.value, 0, len)) {
            return entry.value;
        } else {
            return subString(buffer, offset, len);
        }
    }

    private static String subString(String src, int offset, int len) {
        char[] chars = new char[len];
        src.getChars(offset, offset + len, chars, 0);
        return new String(chars);
    }

    public static int hash(char[] buffer, int offset, int len) {
        int h = 0;
        int off = offset;
        int i = 0;
        while (i < len) {
            h = (h * 31) + buffer[off];
            i++;
            off++;
        }
        return h;
    }

    static class Entry {
        final char[] chars;
        final int hashCode;
        final String value;

        Entry(String value2, int hashCode2) {
            this.value = value2;
            this.chars = value2.toCharArray();
            this.hashCode = hashCode2;
        }
    }
}
