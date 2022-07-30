package com.alibaba.fastjson;

public enum PropertyNamingStrategy {
    CamelCase,
    PascalCase,
    SnakeCase,
    KebabCase;

    /* renamed from: com.alibaba.fastjson.PropertyNamingStrategy$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy = null;

        static {
            $SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy = new int[PropertyNamingStrategy.values().length];
            try {
                $SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy[PropertyNamingStrategy.SnakeCase.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy[PropertyNamingStrategy.KebabCase.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy[PropertyNamingStrategy.PascalCase.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy[PropertyNamingStrategy.CamelCase.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public String translate(String propertyName) {
        char ch;
        int i = AnonymousClass1.$SwitchMap$com$alibaba$fastjson$PropertyNamingStrategy[ordinal()];
        if (i == 1) {
            StringBuilder buf = new StringBuilder();
            for (int i2 = 0; i2 < propertyName.length(); i2++) {
                char ch2 = propertyName.charAt(i2);
                if (ch2 < 'A' || ch2 > 'Z') {
                    buf.append(ch2);
                } else {
                    char ch_ucase = (char) (ch2 + ' ');
                    if (i2 > 0) {
                        buf.append('_');
                    }
                    buf.append(ch_ucase);
                }
            }
            return buf.toString();
        } else if (i == 2) {
            StringBuilder buf2 = new StringBuilder();
            for (int i3 = 0; i3 < propertyName.length(); i3++) {
                char ch3 = propertyName.charAt(i3);
                if (ch3 < 'A' || ch3 > 'Z') {
                    buf2.append(ch3);
                } else {
                    char ch_ucase2 = (char) (ch3 + ' ');
                    if (i3 > 0) {
                        buf2.append('-');
                    }
                    buf2.append(ch_ucase2);
                }
            }
            return buf2.toString();
        } else if (i == 3) {
            char ch4 = propertyName.charAt(0);
            if (ch4 < 'a' || ch4 > 'z') {
                return propertyName;
            }
            char[] chars = propertyName.toCharArray();
            chars[0] = (char) (chars[0] - ' ');
            return new String(chars);
        } else if (i != 4 || (ch = propertyName.charAt(0)) < 'A' || ch > 'Z') {
            return propertyName;
        } else {
            char[] chars2 = propertyName.toCharArray();
            chars2[0] = (char) (chars2[0] + ' ');
            return new String(chars2);
        }
    }
}
