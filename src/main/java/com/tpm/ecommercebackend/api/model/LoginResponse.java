package com.tpm.ecommercebackend.api.model;

/**
 * Response for login requests
 */
public class LoginResponse {

    /** The JWT token to be used for auth requests */
    private String jwt;
    private boolean success;

    /** The reason for login failure  */
    private String failureReason;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
