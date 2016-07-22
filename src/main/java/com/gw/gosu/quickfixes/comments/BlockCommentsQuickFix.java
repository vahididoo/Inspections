
package com.gw.gosu.quickfixes.comments;

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

public class BlockCommentsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * @param elem
     */
    public BlockCommentsQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        String quickfixTooltipName = "Refactor into single line comment";
        return quickfixTooltipName;
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "Block Comments";
    }

    /**
     * @param project
     * @param file
     * @param startElement
     * @param endElement
     * @return
     */
    @Override
    public final boolean isAvailable(@NotNull final Project project,
                                     @NotNull final PsiFile file,
                                     @NotNull final PsiElement startElement,
                                     @NotNull final PsiElement endElement) {
        return startElement.isValid()
                && startElement.getManager().isInProject(startElement);
    }

    /**
     * Quickfix part of code
     * @param project
     * @param file
     * @param editor
     * @param startElement
     * @param endElement
     */
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
            GosuCommentImpl token = (GosuCommentImpl) startElement;
            StringBuilder result = new StringBuilder();
            String commentsPart = "";
            if (token.getElementType().toString().equals("_multiline_comment_")) {
                int endofTextComments = 0;
                //Get each character of the block comment (exclude "/**/")
                for (int counter = 2; counter < token.getTextLength() - 2; counter++) {
                    //ASCII Code - all valid characters (exclude whitespace)
                    for (int count = 32; count < 127; count++){
                        //Get text message
                        if (token.charAt(counter) == count){
                            result.append(token.charAt(counter));
                            endofTextComments++;
                        }
                        //Terminates the loop once reached the end of the text message
                        if (endofTextComments != 0 && token.charAt(counter) == '\n'){
                            count = 127;
                            counter = token.getTextLength();
                        }
                    }
                }
                commentsPart = result.toString();
            }

            String reformattedName = "//" + commentsPart;
            file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                    startElement.getTextOffset() + startElement.getTextLength(),
                    reformattedName);
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}
