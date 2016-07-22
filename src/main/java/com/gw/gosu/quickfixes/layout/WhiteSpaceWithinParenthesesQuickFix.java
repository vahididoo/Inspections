
package com.gw.gosu.quickfixes.layout;

import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * The quick-fix fixes it also based on IntelliJ Gosu Code Style settings – Spaces.
 */
public class WhiteSpaceWithinParenthesesQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    public WhiteSpaceWithinParenthesesQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        String quickfixTooltipsName = "Add whitespaces within open and close brackets";
        return quickfixTooltipsName;
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "White Spaces Within Open and Close Brackets";
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
     * Quick-fix part of code
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
            CodeStyleManager codeStyleManager = new CodeStyleManagerImpl(startElement.getProject());
            codeStyleManager.reformat(startElement.getParent());
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}
