package com.gw.gosu.quickfixes.naminganddeclarations;

import com.gw.gosu.inspections.SampleBundle;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.RefactoringActionHandlerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The quick fix for a single-letter variable name is just to invoke the
 * refactor dialog.
 */
public class SingleLetterVarNameQuickFix
        extends LocalQuickFixAndIntentionActionOnPsiElement {
    /**
     * The name of the variable.
     */
    private final String _varName;

    public SingleLetterVarNameQuickFix(final PsiElement psiElement) {
        super(psiElement);
        _varName = psiElement.getText();
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull PsiFile psiFile,
                       @Nullable("is null when called from inspection") Editor editor,
                       @NotNull PsiElement psiElement,
                       @NotNull PsiElement psiElement2) {
        RefactoringActionHandler renameHandler =
                RefactoringActionHandlerFactory.getInstance().createRenameHandler();
        renameHandler.invoke(project,
                new PsiElement[] { psiElement },
                SimpleDataContext.getProjectContext(project));
    }

    @NotNull
    @Override
    public String getText() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.singlelettervarnameformat", _varName);
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Rename";
    }
}
