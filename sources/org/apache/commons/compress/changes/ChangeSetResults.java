package org.apache.commons.compress.changes;

import java.util.ArrayList;
import java.util.List;

public class ChangeSetResults {
    private final List<String> addedFromChangeSet = new ArrayList();
    private final List<String> addedFromStream = new ArrayList();
    private final List<String> deleted = new ArrayList();

    /* access modifiers changed from: package-private */
    public void deleted(String fileName) {
        this.deleted.add(fileName);
    }

    /* access modifiers changed from: package-private */
    public void addedFromStream(String fileName) {
        this.addedFromStream.add(fileName);
    }

    /* access modifiers changed from: package-private */
    public void addedFromChangeSet(String fileName) {
        this.addedFromChangeSet.add(fileName);
    }

    public List<String> getAddedFromChangeSet() {
        return this.addedFromChangeSet;
    }

    public List<String> getAddedFromStream() {
        return this.addedFromStream;
    }

    public List<String> getDeleted() {
        return this.deleted;
    }

    /* access modifiers changed from: package-private */
    public boolean hasBeenAdded(String fileName) {
        return this.addedFromChangeSet.contains(fileName) || this.addedFromStream.contains(fileName);
    }
}
