package com.aptech.s09.repository;

/**
 * What a repository throws when the store fails — an <em>unchecked</em> exception, so it does
 * not have to appear in any method signature.
 *
 * <p>Two exceptions are usually better than one here: an unchecked
 * {@code RepositoryException} for "the store broke", and a checked one for "you asked for
 * something impossible". This project keeps the first and stretches an
 * {@link IllegalArgumentException} for the second, which is enough at this size — but say
 * the difference out loud in a code review, because callers can only recover from a failure
 * they can see coming.
 */
public class RepositoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
