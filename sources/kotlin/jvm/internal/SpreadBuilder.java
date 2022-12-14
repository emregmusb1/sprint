package kotlin.jvm.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SpreadBuilder {
    private final ArrayList<Object> list;

    public SpreadBuilder(int size) {
        this.list = new ArrayList<>(size);
    }

    public void addSpread(Object container) {
        if (container != null) {
            if (container instanceof Object[]) {
                Object[] array = (Object[]) container;
                if (array.length > 0) {
                    ArrayList<Object> arrayList = this.list;
                    arrayList.ensureCapacity(arrayList.size() + array.length);
                    for (Object element : array) {
                        this.list.add(element);
                    }
                }
            } else if (container instanceof Collection) {
                this.list.addAll((Collection) container);
            } else if (container instanceof Iterable) {
                for (Object element2 : (Iterable) container) {
                    this.list.add(element2);
                }
            } else if (container instanceof Iterator) {
                Iterator iterator = (Iterator) container;
                while (iterator.hasNext()) {
                    this.list.add(iterator.next());
                }
            } else {
                throw new UnsupportedOperationException("Don't know how to spread " + container.getClass());
            }
        }
    }

    public int size() {
        return this.list.size();
    }

    public void add(Object element) {
        this.list.add(element);
    }

    public Object[] toArray(Object[] a) {
        return this.list.toArray(a);
    }
}
