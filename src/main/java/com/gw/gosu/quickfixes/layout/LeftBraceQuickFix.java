
package com.gw.gosu.quickfixes.layout;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuForEachStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuStatementListImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuWhileStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.members.GosuMethodImpl;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * Fixes a pair of braces by making them conform to the Gosu brackets convention.
 */
public class LeftBraceQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     *
     * @param elem The left curly brace token
     */
    public LeftBraceQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("layout.fix.tooltip.bracketslayout.left");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("layout.fix.tooltip.bracketslayout.left");
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
        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
            return;
        }
        try {

            /*
                The correct structure of a bracketed statement is:
                    1. A single whitespace ' '
                    2. A single left brace '{'
                    3. Potential comments or whitespaces(without a newline)
                    4. A newline character
                    5. <Remaining statements>

                    <BracketStmt> -> <Space> LEFT_BRACE <RestOfBracketStmt>
                    <RestOfBracketStmt> -> <Comment> <RestOfBracketStmt>
                    <RestOfBracketStmt> -> <Space> <RestOfBracketStmt>
                    <RestOfBracketStmt> -> NEWLINE <StmtList>
             */

            String deletedComments;

            //Firstly, move the comments AFTER the left brace
            if (startElement.getParent().getParent() instanceof GosuMethodImpl
                    || startElement.getParent().getParent() instanceof GosuIfStatementImpl
                    || startElement.getParent().getParent() instanceof GosuWhileStatementImpl
                    || startElement.getParent().getParent() instanceof GosuForEachStatementImpl) {
                deletedComments = fixMethod(startElement);
            }
            else {
                deletedComments = fixClass(startElement);
            }

            PsiElement parent = startElement.getParent();

            //Put in all the deleted stuff after the left brace
            ((LeafPsiElement) startElement).replaceWithText(deletedComments);


            //Reformat the code afterwards to the match the user's intellij settings
            CodeStyleManager codeStyleManager = new CodeStyleManagerImpl(parent.getProject());
            codeStyleManager.reformat(parent);

        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    /**
     * Applies the quick fix to a function
     * @param leftbrace The left brace element
     * @return The string containing the comments and whitespace that were deleted
     */
    final String fixMethod(final PsiElement leftbrace)
    {
        //Get the first element in the function definition
        PsiElement current = leftbrace.getParent().getParent().getFirstChild();

        //Skip everything until the right parenthesis to where the body of the function begins
        while (!Common.isElementType(current, GosuElementTypes.TT_OP_paren_right)) {
            current = current.getNextSibling();
        }
        current = current.getNextSibling(); //Skip over the right parenthesis

        String stuffBeforeLeftBrace = getStuffBeforeMethodBody(current);

        return "{ " + stuffBeforeLeftBrace;
    }


    /**
     * Applies the quick fix to a class
     * @param leftbrace The left brace element
     * @return The string containing the comments and whitespace that were deleted
     */
    final String fixClass(final PsiElement leftbrace)
    {
        //Get the first element in the function definition
        PsiElement current = leftbrace;

        //Skip back through the whitespace and comments to where the body of the class begins
        while ((current.getPrevSibling() instanceof PsiWhiteSpaceImpl
                || current.getPrevSibling() instanceof GosuCommentImpl)) {
            current = current.getPrevSibling();
        }

        String stuffBeforeLeftBrace = getStuffBeforeClassBody(current);

        return "{ " + stuffBeforeLeftBrace;
    }

    /**
     * Deletes all the whitespaces and comments that appear until the method body is reached
     * @param elem The starting element
     * @return A string containing all of what was deleted
     */
    final String getStuffBeforeMethodBody(final PsiElement elem) {
        PsiElement current = elem;

        ASTNode removeStart = elem.getNode();

        if (current instanceof PsiWhiteSpace) { //Skip over the first whitespace (This space is not needed)
            current = current.getNextSibling();
        }

        //Store and delete everything that appears before the left brace
        StringBuilder sb = new StringBuilder();
        while (!(current instanceof GosuStatementListImpl)) {
            sb.append(current.getText());
            current = current.getNextSibling();
        }

        ASTNode removeEnd = current.getNode();

        if (removeStart != removeEnd)
            elem.getParent().getNode().removeRange(removeStart, removeEnd);

        return sb.toString();
    }

    /**
     * Deletes all the whitespaces and comments that appear until the method body is reached
     * @param elem The starting element
     * @return A string containing all of what was deleted
     */
    final String getStuffBeforeClassBody(final PsiElement elem) {
        PsiElement current = elem;

        ASTNode removeStart = elem.getNode();

        if (current instanceof PsiWhiteSpace) { //Skip over the first whitespace (This space is not needed)
            current = current.getNextSibling();
        }
        //Store and delete everything that appears before the left brace
        StringBuilder sb = new StringBuilder();
        while (!(current instanceof GosuStatementListImpl || Common.isElementType(current, GosuElementTypes.TT_OP_brace_left))) {
            sb.append(current.getText());
            current = current.getNextSibling();
        }

        ASTNode removeEnd = current.getNode();

        if (removeStart != removeEnd)
            elem.getParent().getNode().removeRange(removeStart, removeEnd);


        return sb.toString();
    }

}