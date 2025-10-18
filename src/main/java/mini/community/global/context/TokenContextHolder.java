package mini.community.global.context;

public final class TokenContextHolder {

    private static final ThreadLocal<TokenContext> contextHolder = new ThreadLocal<>();

    private TokenContextHolder() {}

    public static TokenContext getContext() {
        TokenContext context = contextHolder.get();
        if (context == null) {
            context = new TokenContext();
            context.setUserId(0L);
            contextHolder.set(context);
        }
        return context;
    }

    public static void setContext(TokenContext context) {
        if (context == null) {
            clear();
        }else {
            contextHolder.set(context);
        }
    }

    public static void clear() {
        contextHolder.remove();
    }
}
