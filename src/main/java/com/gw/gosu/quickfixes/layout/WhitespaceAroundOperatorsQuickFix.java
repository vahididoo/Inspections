
package com.gw.gosu.quickfixes.layout;

import com.gw.gosu.inspections.SampleBundle;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * Based on IntelliJ settings Gosu Code Style Around Operators. The quick-fix fixes it also based on IntelliJ Gosu Code Style settings – Spaces - around operators.
 */

public class WhitespaceAroundOperatorsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());


    public WhitespaceAroundOperatorsQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("layout.fix.tooltip.whitespacearoundoperators");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("layout.fix.tooltip.whitespacearoundoperators");
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
                        @Nullable ("is null when called from inspection") final Editor editor,
                        @NotNull final PsiElement startElement,
                        @NotNull final PsiElement endElement) {
        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile()) ) {
            return;
        }

        // I think this can be made way more efficient. It works for the moment anyway.
        try {
            CodeStyleManager codeStyleManager = new CodeStyleManagerImpl(startElement.getProject());
            codeStyleManager.reformat(startElement.getParent());
        }
        catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}