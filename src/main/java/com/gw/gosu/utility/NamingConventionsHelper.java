package com.gw.gosu.utility;

/**
 * Helper functions for naming convention type com.gw.gosu.inspections.
 */
public abstract class NamingConventionsHelper {

    /**
     * Checks if a class name starts with an uppercase letter and contains no underscores.
     * @param className The name of the class
     * @return True if the class name adheres to the standards
     */
    public static boolean isWellFormattedClass(final String className) {
        return uppercaseFirstLetter(className) && !hasUnderscores(className);
    }

    /**
     * Check if the first letter in the name is uppercase.
     * @param text The text to be checked
     * @return True if the first letter is uppercase
     */
    public static boolean uppercaseFirstLetter(final String text) {
        return Character.isUpperCase(text.charAt(0));
    }

    /**
     * Check if the name contains underscores other than the first character.
     * @param text The text to be checked
     * @return True if the text contains no underscores
     */
    public static boolean hasUnderscores(final String text) {
        return (text == null)
                || (text.substring(1, text.length()).contains("_"));
    }

    /**
     * Remove underscores and capitalise the following character
     * Underscores are acceptable if they are the first character
     * in the string.
     *
     * @param text The current existing name of the element
     * @return The element's text without underscores.
     */
    public static String removeUnderscores(final String text) {
        return text.replace("_", "");
    }
}
