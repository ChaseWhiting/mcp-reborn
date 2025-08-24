package net.minecraft.util;

public final class MatchException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 0L;

    /**
     * Constructs an {@code MatchException} with the specified detail message and
     * cause.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method). (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public MatchException(String message, Throwable cause) {
        super(message, cause);
    }
}