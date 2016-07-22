
package com.gw.gosu.utility;

import com.intellij.psi.PsiElement;
import gw.plugin.ij.lang.GosuCommentImpl;

/**
 * A class that accumulates comments from PsiElements into a String.
 * It places each comment on a separate line, separated by a new line character
 */
public class CommentExtractor {
    /**
     * Stores all of the comments that have been extracted so far.
     */
    private StringBuilder commentsExtracted;

    /**
     * The indentation created when the class is instantiated
     */
    private final String indentation;

    /**
     * Constructor.
     *
     * @param indent The number of spaces of indentation required before each comment
     */
    public CommentExtractor(final int indent) {
        commentsExtracted = new StringBuilder();
        StringBuilder indentBuilder = new StringBuilder();

        for (int i = 0; i < indent; i++) {
            indentBuilder.append(' ');
        }
        indentation = indentBuilder.toString();
    }

    /**
     * Extracts each comment within the subtree of elem
     * or if elem is a comment, it stores the text of that.
     *
     * @param elem The element from which comments will be extracted
     */
    public final void extractComments(final PsiElement elem) {
        PsiElement child = elem.getFirstChild();

        if (child == null) { //We are at a leaf node, if there are no children
            if (elem instanceof GosuCommentImpl) {

                if (commentsExtracted.length() != 0) {
                    commentsExtracted.append('\n');
                }
                commentsExtracted.append(indentation);
                commentsExtracted.append(elem.getText());
            }
        } else { //Otherwise recurse over the children elements
            while (child != null) {
                extractComments(child);

                child = child.getNextSibling();
            }
        }
    }

    /**
     * Gets a string of all the comments extracted so far.
     *
     * @return All the comments extracted so far
     */
    public final String toString() {
        return commentsExtracted.toString();
    }

}
