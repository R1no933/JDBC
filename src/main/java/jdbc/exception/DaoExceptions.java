package jdbc.exception;

public class DaoExceptions extends RuntimeException {
    public DaoExceptions(Throwable throwable) {
        super(throwable);
    }
}
