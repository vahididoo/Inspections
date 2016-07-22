
package com.gw.gosu.inspections.comments;

import com.gw.gosu.quickfixes.comments.TrailingCommentsQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuClassDefinition;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.statements.GosuAssignmentStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuForEachStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuWhileStatementImpl;
import com.gw.gosu.inspections.SampleBundle;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Checks for trailing comments on consecutive lines that don't have consistent indentation.
 */
public class TrailingComments extends BaseLocalInspectionTool {
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
        return SampleBundle.message("comments.name.trailingcomments");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "GosuTrailingComments";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitAssignmentStatement(final GosuAssignmentStatementImpl elem) //Must visit assignment statements, because these are not visited by visitElement
            {
                visitStatement(elem);
            }

            @Override
            public void visitField(final IGosuField elem) //Must visit field declarations, because these are not visited by visitElement
            {
                visitStatement(elem);
            }

            @Override
            public void visitIfStatement(final GosuIfStatementImpl elem) //Must visit if statements, because these are not visited by visitElement
            {
                visitStatement(elem);
            }

            @Override
            public void visitWhileStatement(final GosuWhileStatementImpl elem) //Must visit while loops, because these are not visited by visitElement
            {
                visitStatement(elem);
            }

            @Override
            public void visitForEachStatement(final GosuForEachStatementImpl elem) {
                visitStatement(elem);
            }

            @Override
            public void visitClassDefinition(final IGosuClassDefinition elem) {
                visitStatement(elem);
            }

            @Override
            public void visitElement(final PsiElement elem) {
                visitStatement(elem);
            }

            private void visitStatement(final PsiElement elem) {
                if (isPrecededByUncommentedLine(elem)  //Start with a line that has no trailing comments on it
                        || elem.getPrevSibling() instanceof PsiIdentifier) { //Or if it's the first line in the file

                    // Get the list of trailing comments first
                    ArrayList<PsiElement> commentsFound = new ArrayList<PsiElement>();
                    getTrailingComments(elem, commentsFound);

                    if (commentsFound.size() > 1) {
                        int max = getMaxIndent(commentsFound);

                        ArrayList<PsiElement> problemElements = new ArrayList<PsiElement>();

                        //Firstly, find all the comments that arent aligned correctly
                        for (int i = 0; i < commentsFound.size(); i++) {
                            int diff = max - Common.getIndent(commentsFound.get(i)); // get the diff distance between max and each one
                            if (diff > 0) {
                                problemElements.add(commentsFound.get(i));
                            }
                        }

                        if (problemElements.size() > 0) {
                            //Next, post a warning for each element
                            //ONLY the first one gets a quick fix (This prevent corruption of the file from multiple conflicting quick fixes)
                            holder.registerProblem(problemElements.get(0), SampleBundle.message("comments.name.trailingcomments"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new TrailingCommentsQuickFix(problemElements, max));

                            //Post only warnings for the rest
                            for (int i = 1; i < problemElements.size(); i++) {
                                holder.registerProblem(problemElements.get(i), SampleBundle.message("comments.name.trailingcomments"),
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                            }
                        }
                    }
                }
                super.visitElement(elem);
            }
        };
    }

    /**
     * Gets an arraylist of consecutive trailing comments.
     * @param elem The element on the line containing the first trailing comment
     * @param commentsFound The comments that are found are stored in this
     * @return Returns false if the stream of consecutive trailing comments is broken
     */
    private static boolean getTrailingComments(final PsiElement elem, final ArrayList<PsiElement> commentsFound) {

        PsiElement current = elem;

        while (current != null) {
            //Get all the children elements as well
            //e.g. Check all the elements within an if statement
            //because the next sibling is actually just the closing '}' brace of an if statement
            //which could be multiple lines down in the code

            if (current.getFirstChild() != null
                && !getTrailingComments(current.getFirstChild(), commentsFound)) {
                return false;
            }

            if (current instanceof GosuCommentImpl && current.getText().startsWith("//")) {
                if (!(current.getPrevSibling() instanceof PsiWhiteSpaceImpl)
                        || (current.getPrevSibling() != null
                        && !current.getPrevSibling().textContains('\n'))) { // Must use contains("\n")
                                                                            // New line whitespace isn't always starting from the \n unless after saved.
                    commentsFound.add(current);
                } else {
                    return false;
                }

                current = current.getNextSibling(); //Skip the newline after the trailing comment

                if (current == null || //make sure we're not looking at a null element
                        StringUtils.countMatches(current.getText(), "\n") >= 2) { //and make sure it's not a double newline
                    return false;
                }
            } else if (current instanceof PsiWhiteSpaceImpl) { //If we find a newline character before we find a comment, then stop the search
                if (current.textContains('\n')) { //No comment on this line
                    return false;
                }
            }

            if (current.getNextSibling() == null) { //Make sure there is actually a token following the comment
                // If this element is at the top of the tree
                if (current.getParent() instanceof IGosuClassDefinition) {
                    //
                    // Check if the element lexicographically following this one is a comment
                    //

                    PsiElement parent = current.getParent()
                            .getNextSibling(); //Skip over the class definition

                    if (parent instanceof PsiWhiteSpaceImpl) { //Skip over the whitespace
                        //It must be a trailing comment
                        if (parent.textContains('\n')) {
                            return false;
                        } else {
                            parent = parent.getNextSibling();
                        }
                    }

                    if (parent instanceof GosuCommentImpl) {
                        commentsFound.add(parent);
                    }
                }
                return true;
            }

            current = current.getNextSibling();
        }
        return true;
    }

    /**
     * Checks if the line prior to this doesn't have a trailing comment.
     * @param elem The element at the start of the current line
     * @return True if the previous line doesn't contain a trailing comment
     */
    static boolean isPrecededByUncommentedLine(final PsiElement elem) {
        PsiElement previous = elem.getPrevSibling();

        //If the previous token is on the line before this one
        if (previous instanceof PsiWhiteSpaceImpl && previous.textContains('\n')) {
            if (StringUtils.countMatches(previous.getText(), "\n") >= 2) //More than one newline character means there is at least one empty line before this
            {
                return true;
            }

            previous = previous.getPrevSibling();
            //if the previous line didnt end with a comment
            if (!(previous instanceof GosuCommentImpl)) {
                return true;
            } else {
                previous = previous.getPrevSibling();
                //if the only thing on the previous line was a comment)
                if (previous instanceof PsiWhiteSpaceImpl && previous.textContains('\n')) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Given a list of elements, returns the length of the maximum indent.
     * @param elementArrayList The list to be checked
     * @return The largest indent of the given list
     */
    private static int getMaxIndent(final ArrayList<PsiElement> elementArrayList) {
        int max = 0;
        for (int i = 0; i < elementArrayList.size(); i++) {
            int tmp = Common.getIndent(elementArrayList.get(i));

            if (tmp > max) {
                max = tmp;
            }
        }

        return max;
    }
}
