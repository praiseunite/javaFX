package com.aptech.a5.repository;

/**
 * The one exception the rest of the program sees when a store fails. GIVEN — do not change it.
 *
 * <p>Unchecked, on purpose. A {@code SQLException} is a fact about H2; a
 * {@link RepositoryException} is a fact about this application. Wrapping the first in the second
 * is what lets {@link StudentRepository} declare no checked exception at all — so a caller that
 * does not care about databases (and none of them should) does not have to write one line about
 * them.
 */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(String message) {
        super(message);
    }
}
