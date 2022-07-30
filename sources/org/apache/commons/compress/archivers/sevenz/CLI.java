package org.apache.commons.compress.archivers.sevenz;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class CLI {

    private enum Mode {
        LIST("Analysing") {
            public void takeAction(SevenZFile archive, SevenZArchiveEntry entry) {
                System.out.print(entry.getName());
                if (entry.isDirectory()) {
                    System.out.print(" dir");
                } else {
                    PrintStream printStream = System.out;
                    printStream.print(" " + entry.getCompressedSize() + "/" + entry.getSize());
                }
                if (entry.getHasLastModifiedDate()) {
                    PrintStream printStream2 = System.out;
                    printStream2.print(" " + entry.getLastModifiedDate());
                } else {
                    System.out.print(" no last modified date");
                }
                if (!entry.isDirectory()) {
                    PrintStream printStream3 = System.out;
                    printStream3.println(" " + getContentMethods(entry));
                    return;
                }
                System.out.println("");
            }

            private String getContentMethods(SevenZArchiveEntry entry) {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (SevenZMethodConfiguration m : entry.getContentMethods()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    first = false;
                    sb.append(m.getMethod());
                    if (m.getOptions() != null) {
                        sb.append("(");
                        sb.append(m.getOptions());
                        sb.append(")");
                    }
                }
                return sb.toString();
            }
        },
        EXTRACT("Extracting") {
            private final byte[] buf;

            public void takeAction(SevenZFile archive, SevenZArchiveEntry entry) throws IOException {
                Throwable th;
                File outFile = new File(entry.getName());
                if (!entry.isDirectory()) {
                    PrintStream printStream = System.out;
                    printStream.println("extracting to " + outFile);
                    File parent = outFile.getParentFile();
                    if (parent == null || parent.exists() || parent.mkdirs()) {
                        OutputStream fos = Files.newOutputStream(outFile.toPath(), new OpenOption[0]);
                        try {
                            long total = entry.getSize();
                            long off = 0;
                            while (off < total) {
                                int bytesRead = archive.read(this.buf, 0, (int) Math.min(total - off, (long) this.buf.length));
                                if (bytesRead >= 1) {
                                    off += (long) bytesRead;
                                    fos.write(this.buf, 0, bytesRead);
                                } else {
                                    throw new IOException("Reached end of entry " + entry.getName() + " after " + off + " bytes, expected " + total);
                                }
                            }
                            if (fos != null) {
                                fos.close();
                                return;
                            }
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        throw new IOException("Cannot create " + parent);
                    }
                } else if (outFile.isDirectory() || outFile.mkdirs()) {
                    PrintStream printStream2 = System.out;
                    printStream2.println("created directory " + outFile);
                    return;
                } else {
                    throw new IOException("Cannot create directory " + outFile);
                }
                throw th;
            }
        };
        
        private final String message;

        public abstract void takeAction(SevenZFile sevenZFile, SevenZArchiveEntry sevenZArchiveEntry) throws IOException;

        private Mode(String message2) {
            this.message = message2;
        }

        public String getMessage() {
            return this.message;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0063, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0067, code lost:
        if (r3 != null) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x006d, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006e, code lost:
        r3.addSuppressed(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0072, code lost:
        r2.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void main(java.lang.String[] r6) throws java.lang.Exception {
        /*
            int r0 = r6.length
            if (r0 != 0) goto L_0x0007
            usage()
            return
        L_0x0007:
            org.apache.commons.compress.archivers.sevenz.CLI$Mode r0 = grabMode(r6)
            java.io.PrintStream r1 = java.lang.System.out
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r0.getMessage()
            r2.append(r3)
            java.lang.String r3 = " "
            r2.append(r3)
            r3 = 0
            r4 = r6[r3]
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r1.println(r2)
            java.io.File r1 = new java.io.File
            r2 = r6[r3]
            r1.<init>(r2)
            boolean r2 = r1.isFile()
            if (r2 != 0) goto L_0x004e
            java.io.PrintStream r2 = java.lang.System.err
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r1)
            java.lang.String r4 = " doesn't exist or is a directory"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.println(r3)
        L_0x004e:
            org.apache.commons.compress.archivers.sevenz.SevenZFile r2 = new org.apache.commons.compress.archivers.sevenz.SevenZFile
            r2.<init>((java.io.File) r1)
            r3 = 0
        L_0x0054:
            org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry r4 = r2.getNextEntry()     // Catch:{ Throwable -> 0x0065 }
            r5 = r4
            if (r4 == 0) goto L_0x005f
            r0.takeAction(r2, r5)     // Catch:{ Throwable -> 0x0065 }
            goto L_0x0054
        L_0x005f:
            r2.close()
            return
        L_0x0063:
            r4 = move-exception
            goto L_0x0067
        L_0x0065:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0063 }
        L_0x0067:
            if (r3 == 0) goto L_0x0072
            r2.close()     // Catch:{ Throwable -> 0x006d }
            goto L_0x0075
        L_0x006d:
            r5 = move-exception
            r3.addSuppressed(r5)
            goto L_0x0075
        L_0x0072:
            r2.close()
        L_0x0075:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.compress.archivers.sevenz.CLI.main(java.lang.String[]):void");
    }

    private static void usage() {
        System.out.println("Parameters: archive-name [list|extract]");
    }

    private static Mode grabMode(String[] args) {
        if (args.length < 2) {
            return Mode.LIST;
        }
        return (Mode) Enum.valueOf(Mode.class, args[1].toUpperCase());
    }
}
