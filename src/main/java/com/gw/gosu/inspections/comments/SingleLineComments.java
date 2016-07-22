
package com.gw.gosu.inspections.comments;

import com.gw.gosu.quickfixes.comments.SingleLineCommentsQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks for multiple lines of consecutive single line comments.
 */
public class SingleLineComments extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("comments.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("comments.name.singlelinecomments");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "SingleLineComments";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {

            @Override
            public void visitElement(final PsiElement elem) {
                if ((isSingleLineComment(elem)
                        && isPrecededByUncommentedLine(elem))) { //Start with a line that has no full line comments preceding it
                    // Get the list of single line comments first
                    PsiElement lastComment = getLastSingleLineComment(elem);

                    if (!elem.equals(lastComment)) {
                        //Select the range in which we want to highlight
                        TextRange range = new TextRange(0, lastComment.getTextLength());

                        //Create the list of quick fixes for the problem
                        LocalQuickFix[] fixes = new LocalQuickFix[] {new SingleLineCommentsQuickFix(elem, lastComment)};

                        //Create the descriptor for the problem
                        ProblemDescriptor descriptor = new ProblemDescriptorImpl(elem, lastComment, SampleBundle.message("comments.name.singlelinecomments"), fixes, ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                false, range, isOnTheFly);

                        //Register the warning
                        holder.registerProblem(descriptor);
                    }
                }
                super.visitElement(elem);
            }
        };
    }

    /**
     * Checks if the element is a single line comment.
     *
     * @param elem A comment element
     * @return True if the element is a single line comment, immediately preceded by a newline
     */
    private static boolean isSingleLineComment(final PsiElement elem) {
        if (Common.isElementType(elem, GosuElementTypes.TT_COMMENT_LINE)) { //This is a single line comment
            if (elem.getPrevSibling() instanceof PsiWhiteSpaceImpl
                    && elem.getPrevSibling().textContains('\n') //Make sure it's not a trailing comment
                    || elem.getPrevSibling() instanceof PsiIdentifier) { //If it's at the start of the file it's a single line comment
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is a single line comment on the line before this element.
     *
     * @param elem The element at the start of this line
     * @return True if the previous line isn't just a comment on its own
     */
    private static boolean isPrecededByUncommentedLine(final PsiElement elem) {
        PsiElement current = elem.getPrevSibling();

        if (current instanceof PsiIdentifier) {
            return true;
        } else if (current instanceof PsiWhiteSpace && StringUtils.countMatches(current.getText(), "\n") > 1) { //There's an empty line before this one
            return true;
        } else if (StringUtils.countMatches(current.getText(), "\n") == 1) {
            current = current.getPrevSibling(); //Skip past the whitespace
            if (Common.isElementType(current, GosuElementTypes.TT_COMMENT_LINE)) { //The line before this is commented; however, trailing comments are allowed
                current = current.getPrevSibling();
                if (current instanceof PsiWhiteSpace
                        && StringUtils.countMatches(current.getText(), "\n") == 0) { //If the comment has no newline immediately before it, then it must be a trailing comment
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the last single line comment that occurs after the starting one.
     *
     * @param elem The starting single line comment
     * @return A single line comment
     */
    private static PsiElement getLastSingleLineComment(final PsiElement elem) {
        PsiElement current = elem;

        while (current != null) {

            if (current.getNextSibling() == null
                    || current.getNextSibling().getNextSibling() == null) {
                //We're at the end of the file
                return current;
            }

            if (StringUtils.countMatches(current.getNextSibling().getText(), "\n") > 1) {
                //If there is a blank line on its own
                return current;
            }

            if (!Common.isElementType(current.getNextSibling().getNextSibling(), GosuElementTypes.TT_COMMENT_LINE)) {
                return current;
            }
            current = current.getNextSibling() //Skip over the current comment
                            .getNextSibling(); //Skip over the whitespace
        }

        return current;
    }
}
