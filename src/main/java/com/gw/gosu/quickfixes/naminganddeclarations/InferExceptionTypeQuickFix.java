
package com.gw.gosu.quickfixes.naminganddeclarations;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.CommentExtractor;
import com.gw.gosu.utility.Common;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * Quick fix that removes the 'Exception' type declaration.
 */
public class InferExceptionTypeQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    /** Logger. */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     * @param elem The variable identifier
     */
    public InferExceptionTypeQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.inferexceptiontype");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.inferexceptiontype");
    }

    @Override
    public final boolean isAvailable(@NotNull final Project project,
                               @NotNull final PsiFile file,
                               @NotNull final PsiElement startElement,
                               @NotNull final PsiElement endElement) {
        return startElement.isValid()
                && startElement.getManager().isInProject(startElement);
    }

    @Override
    public final void invoke(@NotNull final Project project,
                       @NotNull final PsiFile file,
                       @Nullable("is null when called from inspection") final Editor editor,
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {

        PsiElement catchBody = startElement;

        endElement = catchBody.getLastChild();

        startElement = catchBody.getFirstChild();
        startElement = startElement.getNextSibling(); //Skip over the exception identifier (i.e. the variable name)

        while (startElement != null
                && !Common.isElementType(startElement, GosuElementTypes.TT_OP_colon)) { //Skip everything until the ':' character is reached
            startElement = startElement.getNextSibling();
        }

        PsiElement current = startElement;
        CommentExtractor comments = new CommentExtractor(Common.getStartOfLineIndent(startElement));

        //Save all the comments
        while (current != null) {
            comments.extractComments(current);
            current = current.getNextSibling();
        }

        if (Common.isElementType(startElement, GosuElementTypes.TT_OP_colon)) //Ensure that it's the colon character
        {
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
}
