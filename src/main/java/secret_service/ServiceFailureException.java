package secret_service;

/**
 * Created by Martina on 26.3.2015.
 *
 * What a boring name, but as you wish :D
 */
public class ServiceFailureException extends RuntimeException {

    public ServiceFailureException(String msg) {
        super(msg);
    }

    public ServiceFailureException(Throwable cause) {
        super(cause);
    }

    public ServiceFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
