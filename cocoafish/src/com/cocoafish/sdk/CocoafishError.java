package com.cocoafish.sdk;

/**
 * Encapsulation of a Cocoafish Error: a Cocoafish request that could not be
 * fulfilled.
 *
 */
public class CocoafishError extends Throwable {

    private static final long serialVersionUID = 1L;

    private int mErrorCode = 0;

    public CocoafishError(String message) {
        super(message);
    }

    public CocoafishError(String message, int code) {
        super(message);
        mErrorCode = code;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

}
