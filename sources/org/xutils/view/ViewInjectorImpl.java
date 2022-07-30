package org.xutils.view;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import org.xutils.ViewInjector;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public final class ViewInjectorImpl implements ViewInjector {
    private static final HashSet<Class<?>> IGNORED = new HashSet<>();
    private static volatile ViewInjectorImpl instance;
    private static final Object lock = new Object();

    static {
        IGNORED.add(Object.class);
        IGNORED.add(Activity.class);
        IGNORED.add(Fragment.class);
        try {
            IGNORED.add(Class.forName("androidx.fragment.app.Fragment"));
            IGNORED.add(Class.forName("androidx.fragment.app.FragmentActivity"));
        } catch (Throwable th) {
        }
    }

    private ViewInjectorImpl() {
    }

    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ViewInjectorImpl();
                }
            }
        }
        x.Ext.setViewInjector(instance);
    }

    public void inject(View view) {
        injectObject(view, view.getClass(), new ViewFinder(view));
    }

    public void inject(Activity activity) {
        int viewId;
        Class<?> handlerType = activity.getClass();
        try {
            ContentView contentView = findContentView(handlerType);
            if (contentView != null && (viewId = contentView.value()) > 0) {
                activity.setContentView(viewId);
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
        injectObject(activity, handlerType, new ViewFinder(activity));
    }

    public void inject(Object handler, View view) {
        injectObject(handler, handler.getClass(), new ViewFinder(view));
    }

    public View inject(Object fragment, LayoutInflater inflater, ViewGroup container) {
        int viewId;
        View view = null;
        Class<?> handlerType = fragment.getClass();
        try {
            ContentView contentView = findContentView(handlerType);
            if (contentView != null && (viewId = contentView.value()) > 0) {
                view = inflater.inflate(viewId, container, false);
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
        injectObject(fragment, handlerType, new ViewFinder(view));
        return view;
    }

    private static ContentView findContentView(Class<?> thisCls) {
        if (thisCls == null || IGNORED.contains(thisCls) || thisCls.getName().startsWith("androidx.")) {
            return null;
        }
        ContentView contentView = (ContentView) thisCls.getAnnotation(ContentView.class);
        if (contentView == null) {
            return findContentView(thisCls.getSuperclass());
        }
        return contentView;
    }

    private static void injectObject(Object handler, Class<?> handlerType, ViewFinder finder) {
        Event event;
        ViewInject viewInject;
        Object obj = handler;
        Class<?> cls = handlerType;
        ViewFinder viewFinder = finder;
        if (cls != null && !IGNORED.contains(cls) && !handlerType.getName().startsWith("androidx.")) {
            injectObject(obj, handlerType.getSuperclass(), viewFinder);
            Field[] fields = handlerType.getDeclaredFields();
            int i = 0;
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    Class<?> fieldType = field.getType();
                    if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()) && !fieldType.isPrimitive() && !fieldType.isArray() && (viewInject = (ViewInject) field.getAnnotation(ViewInject.class)) != null) {
                        try {
                            View view = viewFinder.findViewById(viewInject.value(), viewInject.parentId());
                            if (view != null) {
                                field.setAccessible(true);
                                field.set(obj, view);
                            } else {
                                throw new RuntimeException("Invalid @ViewInject for " + handlerType.getSimpleName() + "." + field.getName());
                            }
                        } catch (Throwable ex) {
                            LogUtil.e(ex.getMessage(), ex);
                        }
                    }
                }
            }
            Method[] methods = handlerType.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                int length = methods.length;
                int i2 = 0;
                while (i2 < length) {
                    Method method = methods[i2];
                    if (!Modifier.isStatic(method.getModifiers()) && Modifier.isPrivate(method.getModifiers()) && (event = (Event) method.getAnnotation(Event.class)) != null) {
                        try {
                            int[] values = event.value();
                            int[] parentIds = event.parentId();
                            int parentIdsLen = parentIds == null ? 0 : parentIds.length;
                            int i3 = i;
                            while (i3 < values.length) {
                                int value = values[i3];
                                if (value > 0) {
                                    ViewInfo info = new ViewInfo();
                                    info.value = value;
                                    try {
                                        info.parentId = parentIdsLen > i3 ? parentIds[i3] : 0;
                                        method.setAccessible(true);
                                        EventListenerManager.addEventMethod(viewFinder, info, event, obj, method);
                                    } catch (Throwable th) {
                                        ex = th;
                                        LogUtil.e(ex.getMessage(), ex);
                                        i2++;
                                        i = 0;
                                    }
                                }
                                i3++;
                            }
                        } catch (Throwable th2) {
                            ex = th2;
                        }
                    }
                    i2++;
                    i = 0;
                }
            }
        }
    }
}
