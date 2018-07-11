package it.dturek.cloudhosting.domain;

public enum AuthenticationError {

    BAD_CREDENTIALS(0, "login.error.bad_credentials"),
    NOT_ENABLED(1, "login.error_account_not_enabled");

    private int code;
    private String message;

    AuthenticationError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static AuthenticationError fromCode(int code) {
        for (AuthenticationError authenticationError : AuthenticationError.values()) {
            if (authenticationError.getCode() == code) {
                return authenticationError;
            }
        }
        return null;
    }
}
