package boot.exception;

/**
 * @Description TODO
 * @Date 2019/11/20 13:33
 * @Author huangyangyang
 */
public class LocalizedException extends RuntimeException{
    @Getter
    private final Object[] localeArgs;
    @Getter
    private final String localeMessage;

    public LocalizedException(String localeMessage, String message) {
        super(message);
        this.localeArgs = new Object[0];
        this.localeMessage = localeMessage;
    }

    public LocalizedException(String localeMessage, Object[] localeArgs, String message) {
        super(message);
        this.localeArgs = localeArgs;
        this.localeMessage = localeMessage;
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }
}
