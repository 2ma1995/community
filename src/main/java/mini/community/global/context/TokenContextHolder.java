package mini.community.global.context;

public class TokenContextHolder {
    private static final ThreadLocal<TokenContext> contextHolder = new ThreadLocal<>();

    public static void setContext(TokenContext context) {
        contextHolder.set(context);
    }

    public static TokenContext getContext() {
        TokenContext context = contextHolder.get();
        if (context == null) {
            context = new TokenContext();
            contextHolder.set(context);
        }
        return context;
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
