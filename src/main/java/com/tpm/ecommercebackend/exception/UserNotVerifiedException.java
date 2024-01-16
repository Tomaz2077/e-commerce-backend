package com.tpm.ecommercebackend.exception;

/**
 * Exception thrown when a user does not have a verified email address
 */
public class UserNotVerifiedException extends Exception{
    private boolean newEmailSent;

    public UserNotVerifiedException(boolean newEmailSent) {
        this.newEmailSent = newEmailSent;
    }

    public boolean isNewEmailSent() {
        return newEmailSent;
    }
}
