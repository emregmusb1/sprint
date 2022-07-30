package butterknife;

import android.util.Property;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import java.util.List;

public final class ViewCollections {
    @UiThread
    @SafeVarargs
    public static <T extends View> void run(@NonNull List<T> list, @NonNull Action<? super T>... actions) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            for (Action<? super T> action : actions) {
                action.apply((View) list.get(i), i);
            }
        }
    }

    @UiThread
    @SafeVarargs
    public static <T extends View> void run(@NonNull T[] array, @NonNull Action<? super T>... actions) {
        int count = array.length;
        for (int i = 0; i < count; i++) {
            for (Action<? super T> action : actions) {
                action.apply(array[i], i);
            }
        }
    }

    @UiThread
    public static <T extends View> void run(@NonNull List<T> list, @NonNull Action<? super T> action) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            action.apply((View) list.get(i), i);
        }
    }

    @UiThread
    public static <T extends View> void run(@NonNull T[] array, @NonNull Action<? super T> action) {
        int count = array.length;
        for (int i = 0; i < count; i++) {
            action.apply(array[i], i);
        }
    }

    @UiThread
    @SafeVarargs
    public static <T extends View> void run(@NonNull T view, @NonNull Action<? super T>... actions) {
        for (Action<? super T> action : actions) {
            action.apply(view, 0);
        }
    }

    @UiThread
    public static <T extends View> void run(@NonNull T view, @NonNull Action<? super T> action) {
        action.apply(view, 0);
    }

    @UiThread
    public static <T extends View, V> void set(@NonNull List<T> list, @NonNull Setter<? super T, V> setter, @Nullable V value) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            setter.set((View) list.get(i), value, i);
        }
    }

    @UiThread
    public static <T extends View, V> void set(@NonNull T[] array, @NonNull Setter<? super T, V> setter, @Nullable V value) {
        int count = array.length;
        for (int i = 0; i < count; i++) {
            setter.set(array[i], value, i);
        }
    }

    @UiThread
    public static <T extends View, V> void set(@NonNull T view, @NonNull Setter<? super T, V> setter, @Nullable V value) {
        setter.set(view, value, 0);
    }

    @UiThread
    public static <T extends View, V> void set(@NonNull List<T> list, @NonNull Property<? super T, V> setter, @Nullable V value) {
        int count = list.size();
        for (int i = 0; i < count; i++) {
            setter.set(list.get(i), value);
        }
    }

    @UiThread
    public static <T extends View, V> void set(@NonNull T[] array, @NonNull Property<? super T, V> setter, @Nullable V value) {
        for (T t : array) {
            setter.set(t, value);
        }
    }

    @UiThread
    public static <T extends View, V> void set(@NonNull T view, @NonNull Property<? super T, V> setter, @Nullable V value) {
        setter.set(view, value);
    }

    private ViewCollections() {
    }
}
