package rxjava.android.com.rxjavastudy.schedule;


public final class ContextManager {
    static final ThreadLocal<Object> ctx = new ThreadLocal<>();

    private ContextManager() {
        throw new IllegalStateException();
    }

    public static Object get() {
        return ctx.get();
    }

    public static void set(Object context) {
        ctx.set(context);
    }
}
