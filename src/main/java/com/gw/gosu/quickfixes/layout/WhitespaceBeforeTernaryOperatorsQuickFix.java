
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
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * The quick-fix fixes it also based on IntelliJ Gosu Code Style settings – Spaces – In-Ternary Operators.
 */
public class WhitespaceBeforeTernaryOperatorsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    public WhitespaceBeforeTernaryOperatorsQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("layout.fix.tooltip.whitespacebeforeternaryoperators");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("layout.fix.tooltip.whitespacebeforeternaryoperators");
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
                             @NotNull final PsiElement startElement,
                             @NotNull final PsiElement endElement) {
        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile()) ) {
            return;
        }

        // I think this can be made way more efficient. It works for the moment anyway.
        try {
//Bugs fixed at IntelliJ internal setting - code style - gosu - space - ternary operators. the setting does not function well.
//TODO:            CodeStyleManager codeStyleManager = new CodeStyleManagerImpl(project);
//TODO:            codeStyleManager.reformat(startElement.getParent().getParent());

            String newStatement = "";
            boolean before = false;
            boolean after = false;

            if (startElement.getPrevSibling() instanceof PsiWhiteSpace) {
                before = true;
            }

            if (startElement.getNextSibling() instanceof PsiWhiteSpace) {
                after = true;
            }

            if(!before && !after) {
                newStatement = " " + startElement.getText() + " ";
                file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                        startElement.getTextOffset() + startElement.getText().length(),
                        newStatement);
                return;
            }

            if(!before) {
                newStatement = " " + startElement.getText();
            }

            if(!after) {
                newStatement = startElement.getText() + " ";
            }

            file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                    startElement.getTextOffset() + startElement.getText().length(),
                    newStatement);
        }
        catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}