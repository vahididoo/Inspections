
package com.gw.gosu.quickfixes.potentialbugs;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatementList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.gw.gosu.utility.CommentExtractor;

import java.lang.invoke.MethodHandles;

/**
 * Negates the if condition and deletes the if body and else keyword.
 */
public class EmptyIfWithElseQuickFix
        extends LocalQuickFixAndIntentionActionOnPsiElement {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     *
     * @param elem The entire if statement
     */
    public EmptyIfWithElseQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("potentialbugs.fix.tooltip.emptyifwithelse");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("potentialbugs.fix.tooltip.emptyifwithelse");
    }

    @Override
    public final boolean isAvailable(@NotNull final Project project,
                               @NotNull final PsiFile file,
                               @NotNull final PsiElement startElement,
                               @NotNull final PsiElement endElement) {
        //final GosuTypeLiteralImpl typeLit = (GosuTypeLiteralImpl)startElement;
        return startElement.isValid()
                && startElement.getManager().isInProject(startElement);
    }

    @Override
    public final void invoke(@NotNull final Project project,
                       @NotNull final PsiFile file,
                       @Nullable("is null when called from inspection") final Editor editor,
                       @NotNull final PsiElement startElement,
                       @NotNull final PsiElement endElement) {


        /*Firstly, negate the startElement (Put a ! around the existing brackets, and then add new brackets round them)

         e.g.
             if (x < 3)

             becomes

             if
             (x < 3)

             and then we combine them again with a negation in between and a bracket after

             if (!(x < 3))
        */

        PsiElement current = startElement.getFirstChild();
        //The first child is an 'if' token
        StringBuilder negatedIfText = new StringBuilder(current.getText());
        CommentExtractor comments = new CommentExtractor(Common.getStartOfLineIndent(startElement));

        //Add a new outer opening parenthesis, and negate the expression contained in the existing parentheses
        negatedIfText.append(" (!");

        boolean rightParenthesisReached = false;

        current = current.getNextSibling();
        while (!Common.isElementType(current, GosuElementTypes.TT_else)) {
            if (current instanceof GosuCommentImpl) {
                comments.extractComments(current);
            } else if (!(current instanceof IGosuStatementList) && !Common.isElementType(current, GosuElementTypes.ELEM_TYPE_NoOpStatement)) {
                //Don't include the empty statement

                //Ignore spacing outside of the condition body
                if (!(current instanceof PsiWhiteSpaceImpl && rightParenthesisReached)) {
                    negatedIfText.append(current.getText());
                }

                if (Common.isElementType(current, GosuElementTypes.TT_OP_paren_right)) {
                    rightParenthesisReached = true;

                    //Closing parenthesis is required to match the one we added above!
                    negatedIfText.append(") ");
                }
            } else if (!(current instanceof PsiWhiteSpaceImpl)) {
                //Ignore the whitespace

                //Keep the comments though
                comments.extractComments(current);
            }
            current = current.getNextSibling();
        }

        current = current.getNextSibling(); //Skip over the 'else'

        while (current instanceof PsiWhiteSpaceImpl || current instanceof PsiCommentImpl) { //Skip over whitespace and comments
            comments.extractComments(current);
            current = current.getNextSibling();
        }

        if (current instanceof IGosuStatementList) {
            //Don't add a newline here because the opening curly brace should be immediately after the right parenthesis
            negatedIfText.append(current.getText()); //Add the body of the else
        } else {
            //The next statement should be a line ahead
            negatedIfText.append('\n');
            int indent = Common.getStartOfLineIndent(startElement);
            while (indent + Common.getIndentSize(startElement.getProject(), startElement.getContainingFile().getFileType()) > 0) {
                negatedIfText.append(' ');
                indent--;
            }

            if (current != null) {
                negatedIfText.append(current.getText()); //Add the body of the else
            }
        }

        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
            return;
        }
        try {
            file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                    startElement.getTextOffset() + startElement.getTextLength(), negatedIfText.toString() + '\n' + comments.toString() + '\n'); //Replace the if statement with the negated of statement
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}
