package LinqALike.Common;

public class QueryDelegateException extends RuntimeException{
    public QueryDelegateException(Throwable cause) {
        super(cause);
    }

    public QueryDelegateException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryDelegateException(String message) {
        super(message);
    }

    public QueryDelegateException() {
    }
}

