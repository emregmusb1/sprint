package org.xutils.view;

final class ViewInfo {
    public int parentId;
    public int value;

    ViewInfo() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViewInfo viewInfo = (ViewInfo) o;
        if (this.value == viewInfo.value && this.parentId == viewInfo.parentId) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.value * 31) + this.parentId;
    }
}
