
package com.gw.gosu.quickfixes.comments;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.GosuCommentImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

/**
 * Fixes a group of consecutive single line comments by converting them into one multiline comment.
 */
public class SingleLineCommentsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    /**
     * Logger.
     */
    private static final Logger LOG =
            Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     * @param startElem The first single line comment
     * @param endElem The last single line comment
     */
    public SingleLineCommentsQuickFix(final PsiElement startElem, final PsiElement endElem) {
        super(startElem, endElem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("comments.fix.tooltip.singlelinecomments");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("comments.fix.tooltip.singlelinecomments");
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

        String indent = Common.getStartOfLineIndentStr(startElement);

        StringBuilder newText = new StringBuilder();
        newText.append("/*");

        PsiElement current = startElement;
        while (current != endElement) {
            this.convertSingleLineOfComment(newText, current, indent);
            current = current.getNextSibling();
        }

        this.convertSingleLineOfComment(newText, current, indent);
        newText.append('\n');
        newText.append(indent);
        newText.append("*/");

        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
            return;
        }

        try {
            file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                    endElement.getTextOffset() + endElement.getTextLength(), newText.toString());
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    private void convertSingleLineOfComment(StringBuilder newText, PsiElement current, String lineIndent) {
        String commentText;
        if (current instanceof GosuCommentImpl) {
            // Skip the '//' characters
            int commentIndex = current.getText().indexOf("//");
            commentText = (commentIndex >= 0) ? current.getText().substring(commentIndex + 2) : current.getText();

            newText.append('\n');
            newText.append(lineIndent);
            newText.append(commentText);
        }
    }
}