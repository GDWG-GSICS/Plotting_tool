package org.eumetsat.usd.gcp.client.util;

/**
 * Utility class for login form field verification.
 * 
 * @author USD/C/PBe
 */
public final class FieldVerifier
{
    // @formatter:off
    /* 
     * (        # Start of group
     *  .       # match anything with previous condition checking 
     *  {6,32}  # length at least 6 characters and maximum of 32 
     * )        # End of group
     */ 
    // @formatter:on
    /** Password validation regular expression. */
    private static final String PASSWORD_VALIDATION_REGEX = "(.{6,32})";

    /**
     * Private constructor to make it non-instantiable.
     */
    private FieldVerifier()
    {
    }

    /**
     * Check format validity of username (6 characters min, 32 max).
     * 
     * @param username
     *            User name to be validated.
     * @return if username's format is valid.
     */
    public static boolean isValidUsername(final String username)
    {
        if (username == null)
        {
            return false;
        }
        return (username.length() >= 6 && username.length() <= 32);
    }

    /**
     * Check format validity of password. (6 characters min, 32 max)
     * 
     * @param password
     *            Password to be validated.
     * @return if password's format is valid.
     */
    public static boolean isValidPassword(final String password)
    {
        if (password == null)
        {
            return false;
        }
        return password.matches(PASSWORD_VALIDATION_REGEX);
    }
}
