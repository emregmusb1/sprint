package butterknife.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.AttrRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import java.lang.reflect.Array;
import java.util.List;

public final class Utils {
    private static final TypedValue VALUE = new TypedValue();

    @UiThread
    public static Drawable getTintedDrawable(Context context, @DrawableRes int id, @AttrRes int tintAttrId) {
        if (context.getTheme().resolveAttribute(tintAttrId, VALUE, true)) {
            Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, id).mutate());
            DrawableCompat.setTint(drawable, ContextCompat.getColor(context, VALUE.resourceId));
            return drawable;
        }
        throw new Resources.NotFoundException("Required tint color attribute with name " + context.getResources().getResourceEntryName(tintAttrId) + " and attribute ID " + tintAttrId + " was not found.");
    }

    @UiThread
    public static float getFloat(Context context, @DimenRes int id) {
        TypedValue value = VALUE;
        context.getResources().getValue(id, value, true);
        if (value.type == 4) {
            return value.getFloat();
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
    }

    @SafeVarargs
    public static <T> T[] arrayFilteringNull(T... views) {
        int end = 0;
        for (T view : views) {
            if (view != null) {
                views[end] = view;
                end++;
            }
        }
        if (end == length) {
            return views;
        }
        T[] newViews = (Object[]) Array.newInstance(views.getClass().getComponentType(), end);
        System.arraycopy(views, 0, newViews, 0, end);
        return newViews;
    }

    @SafeVarargs
    public static <T> List<T> listFilteringNull(T... views) {
        return new ImmutableList(arrayFilteringNull(views));
    }

    public static <T> T findOptionalViewAsType(View source, @IdRes int id, String who, Class<T> cls) {
        return castView(source.findViewById(id), id, who, cls);
    }

    public static View findRequiredView(View source, @IdRes int id, String who) {
        View view = source.findViewById(id);
        if (view != null) {
            return view;
        }
        String name = getResourceEntryName(source, id);
        throw new IllegalStateException("Required view '" + name + "' with ID " + id + " for " + who + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }

    public static <T> T findRequiredViewAsType(View source, @IdRes int id, String who, Class<T> cls) {
        return castView(findRequiredView(source, id, who), id, who, cls);
    }

    public static <T> T castView(View view, @IdRes int id, String who, Class<T> cls) {
        try {
            return cls.cast(view);
        } catch (ClassCastException e) {
            String name = getResourceEntryName(view, id);
            throw new IllegalStateException("View '" + name + "' with ID " + id + " for " + who + " was of the wrong type. See cause for more info.", e);
        }
    }

    public static <T> T castParam(Object value, String from, int fromPos, String to, int toPos, Class<T> cls) {
        try {
            return cls.cast(value);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parameter #" + (fromPos + 1) + " of method '" + from + "' was of the wrong type for parameter #" + (toPos + 1) + " of method '" + to + "'. See cause for more info.", e);
        }
    }

    private static String getResourceEntryName(View view, @IdRes int id) {
        if (view.isInEditMode()) {
            return "<unavailable while editing>";
        }
        return view.getContext().getResources().getResourceEntryName(id);
    }

    private Utils() {
        throw new AssertionError("No instances.");
    }
}
