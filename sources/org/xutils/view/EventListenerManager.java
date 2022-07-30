package org.xutils.view;

import android.text.TextUtils;
import android.view.View;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import org.xutils.common.util.DoubleKeyValueMap;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.Event;

final class EventListenerManager {
    /* access modifiers changed from: private */
    public static final HashSet<String> AVOID_QUICK_EVENT_SET = new HashSet<>(2);
    private static final long QUICK_EVENT_TIME_SPAN = 300;
    private static final DoubleKeyValueMap<ViewInfo, Class<?>, Object> listenerCache = new DoubleKeyValueMap<>();

    static {
        AVOID_QUICK_EVENT_SET.add("onClick");
        AVOID_QUICK_EVENT_SET.add("onItemClick");
    }

    private EventListenerManager() {
    }

    public static void addEventMethod(ViewFinder finder, ViewInfo info, Event event, Object handler, Method method) {
        try {
            View view = finder.findViewByInfo(info);
            if (view != null) {
                Class<?> listenerType = event.type();
                String listenerSetter = event.setter();
                if (TextUtils.isEmpty(listenerSetter)) {
                    listenerSetter = "set" + listenerType.getSimpleName();
                }
                String methodName = event.method();
                boolean addNewMethod = false;
                Object listener = listenerCache.get(info, listenerType);
                if (listener != null) {
                    DynamicHandler dynamicHandler = (DynamicHandler) Proxy.getInvocationHandler(listener);
                    addNewMethod = handler.equals(dynamicHandler.getHandler());
                    if (addNewMethod) {
                        dynamicHandler.addMethod(methodName, method);
                    }
                }
                if (!addNewMethod) {
                    DynamicHandler dynamicHandler2 = new DynamicHandler(handler);
                    dynamicHandler2.addMethod(methodName, method);
                    listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, dynamicHandler2);
                    listenerCache.put(info, listenerType, listener);
                }
                view.getClass().getMethod(listenerSetter, new Class[]{listenerType}).invoke(view, new Object[]{listener});
            }
        } catch (Throwable ex) {
            LogUtil.e(ex.getMessage(), ex);
        }
    }

    public static class DynamicHandler implements InvocationHandler {
        private static long lastClickTime = 0;
        private WeakReference<Object> handlerRef;
        private final HashMap<String, Method> methodMap = new HashMap<>(1);

        public DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference<>(handler);
        }

        public void addMethod(String name, Method method) {
            this.methodMap.put(name, method);
        }

        public Object getHandler() {
            return this.handlerRef.get();
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: java.lang.reflect.Method} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Object invoke(java.lang.Object r9, java.lang.reflect.Method r10, java.lang.Object[] r11) throws java.lang.Throwable {
            /*
                r8 = this;
                java.lang.ref.WeakReference<java.lang.Object> r0 = r8.handlerRef
                java.lang.Object r0 = r0.get()
                r1 = 0
                if (r0 == 0) goto L_0x00ed
                java.lang.String r2 = r10.getName()
                java.lang.String r3 = "toString"
                boolean r3 = r3.equals(r2)
                if (r3 == 0) goto L_0x001c
                java.lang.Class<org.xutils.view.EventListenerManager$DynamicHandler> r1 = org.xutils.view.EventListenerManager.DynamicHandler.class
                java.lang.String r1 = r1.getSimpleName()
                return r1
            L_0x001c:
                java.util.HashMap<java.lang.String, java.lang.reflect.Method> r3 = r8.methodMap
                java.lang.Object r3 = r3.get(r2)
                r10 = r3
                java.lang.reflect.Method r10 = (java.lang.reflect.Method) r10
                if (r10 != 0) goto L_0x0059
                java.util.HashMap<java.lang.String, java.lang.reflect.Method> r3 = r8.methodMap
                int r3 = r3.size()
                r4 = 1
                if (r3 != r4) goto L_0x0059
                java.util.HashMap<java.lang.String, java.lang.reflect.Method> r3 = r8.methodMap
                java.util.Set r3 = r3.entrySet()
                java.util.Iterator r3 = r3.iterator()
                boolean r4 = r3.hasNext()
                if (r4 == 0) goto L_0x0059
                java.lang.Object r3 = r3.next()
                java.util.Map$Entry r3 = (java.util.Map.Entry) r3
                java.lang.Object r4 = r3.getKey()
                java.lang.CharSequence r4 = (java.lang.CharSequence) r4
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 == 0) goto L_0x0059
                java.lang.Object r4 = r3.getValue()
                r10 = r4
                java.lang.reflect.Method r10 = (java.lang.reflect.Method) r10
            L_0x0059:
                if (r10 == 0) goto L_0x00c4
                java.util.HashSet r3 = org.xutils.view.EventListenerManager.AVOID_QUICK_EVENT_SET
                boolean r3 = r3.contains(r2)
                if (r3 == 0) goto L_0x0093
                long r3 = java.lang.System.currentTimeMillis()
                long r5 = lastClickTime
                long r3 = r3 - r5
                r5 = 0
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 <= 0) goto L_0x008d
                r5 = 300(0x12c, double:1.48E-321)
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 >= 0) goto L_0x008d
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "onClick cancelled: "
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                org.xutils.common.util.LogUtil.d(r5)
                return r1
            L_0x008d:
                long r5 = java.lang.System.currentTimeMillis()
                lastClickTime = r5
            L_0x0093:
                java.lang.Object r1 = r10.invoke(r0, r11)     // Catch:{ Throwable -> 0x0098 }
                return r1
            L_0x0098:
                r1 = move-exception
                java.lang.RuntimeException r3 = new java.lang.RuntimeException
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "invoke method error:"
                r4.append(r5)
                java.lang.Class r5 = r0.getClass()
                java.lang.String r5 = r5.getName()
                r4.append(r5)
                java.lang.String r5 = "#"
                r4.append(r5)
                java.lang.String r5 = r10.getName()
                r4.append(r5)
                java.lang.String r4 = r4.toString()
                r3.<init>(r4, r1)
                throw r3
            L_0x00c4:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "method not impl: "
                r3.append(r4)
                r3.append(r2)
                java.lang.String r4 = "("
                r3.append(r4)
                java.lang.Class r4 = r0.getClass()
                java.lang.String r4 = r4.getSimpleName()
                r3.append(r4)
                java.lang.String r4 = ")"
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                org.xutils.common.util.LogUtil.w((java.lang.String) r3)
            L_0x00ed:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xutils.view.EventListenerManager.DynamicHandler.invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]):java.lang.Object");
        }
    }
}
