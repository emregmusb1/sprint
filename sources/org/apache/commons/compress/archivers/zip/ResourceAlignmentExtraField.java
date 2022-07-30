package org.apache.commons.compress.archivers.zip;

import java.util.zip.ZipException;
import kotlin.jvm.internal.ShortCompanionObject;

public class ResourceAlignmentExtraField implements ZipExtraField {
    private static final int ALLOW_METHOD_MESSAGE_CHANGE_FLAG = 32768;
    public static final int BASE_SIZE = 2;
    public static final ZipShort ID = new ZipShort(41246);
    private short alignment;
    private boolean allowMethodChange;
    private int padding;

    public ResourceAlignmentExtraField() {
        this.padding = 0;
    }

    public ResourceAlignmentExtraField(int alignment2) {
        this(alignment2, false);
    }

    public ResourceAlignmentExtraField(int alignment2, boolean allowMethodChange2) {
        this(alignment2, allowMethodChange2, 0);
    }

    public ResourceAlignmentExtraField(int alignment2, boolean allowMethodChange2, int padding2) {
        this.padding = 0;
        if (alignment2 < 0 || alignment2 > 32767) {
            throw new IllegalArgumentException("Alignment must be between 0 and 0x7fff, was: " + alignment2);
        } else if (padding2 >= 0) {
            this.alignment = (short) alignment2;
            this.allowMethodChange = allowMethodChange2;
            this.padding = padding2;
        } else {
            throw new IllegalArgumentException("Padding must not be negative, was: " + padding2);
        }
    }

    public short getAlignment() {
        return this.alignment;
    }

    public boolean allowMethodChange() {
        return this.allowMethodChange;
    }

    public ZipShort getHeaderId() {
        return ID;
    }

    public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.padding + 2);
    }

    public ZipShort getCentralDirectoryLength() {
        return new ZipShort(2);
    }

    public byte[] getLocalFileDataData() {
        byte[] content = new byte[(this.padding + 2)];
        ZipShort.putShort(this.alignment | (this.allowMethodChange ? ShortCompanionObject.MIN_VALUE : 0), content, 0);
        return content;
    }

    public byte[] getCentralDirectoryData() {
        return ZipShort.getBytes(this.alignment | (this.allowMethodChange ? ShortCompanionObject.MIN_VALUE : 0));
    }

    public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
        parseFromCentralDirectoryData(buffer, offset, length);
        this.padding = length - 2;
    }

    public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
        if (length >= 2) {
            int alignmentValue = ZipShort.getValue(buffer, offset);
            this.alignment = (short) (alignmentValue & 32767);
            this.allowMethodChange = (32768 & alignmentValue) != 0;
            return;
        }
        throw new ZipException("Too short content for ResourceAlignmentExtraField (0xa11e): " + length);
    }
}
