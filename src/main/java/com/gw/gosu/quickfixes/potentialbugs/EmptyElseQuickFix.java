
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
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.gw.gosu.utility.CommentExtractor;

import java.lang.invoke.MethodHandles;

/**
 * Deletes the else part of an if statement.
 */
public class EmptyElseQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor
     *
     * @param elem The else block
     */
    public EmptyElseQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("potentialbugs.fix.tooltip.emptyelse");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("potentialbugs.fix.tooltip.emptyelse");
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
                       @NotNull PsiElement endElement) {

        endElement = startElement;

        CommentExtractor comments = new CommentExtractor(Common.getStartOfLineIndent(startElement.getParent()));

        while (endElement.getNextSibling() != null) //Keep going until the last child is found
        {
            comments.extractComments(endElement);
            endElement = endElement.getNextSibling();
        }

        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
            return;
        }
        try {
            file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                    endElement.getTextOffset() + endElement.getTextLength(), comments.toString());
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}
