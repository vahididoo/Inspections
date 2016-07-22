
package com.gw.gosu.quickfixes.naminganddeclarations;

import com.gw.gosu.inspections.SampleBundle;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.refactoring.rename.naming.AutomaticRenamerFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.gw.gosu.utility.NamingConventionsHelper;

import java.lang.invoke.MethodHandles;

/**
 * Uses Intellij refactoring api to rename the class to an appropriate name.
 */
public class ClassNameFormatQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    /** Logger. */
    private static final Logger LOG =
            Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * The new name which will be assigned to the variable.
     */
    private String newName;

    /**
     * Constructor.
     * @param elem The identifier of the variable
     */
    public ClassNameFormatQuickFix(final PsiElement elem) {
        super(elem);
        String name = elem.getText();
        String nameNoUnderscores = NamingConventionsHelper.removeUnderscores(name);
        newName =
                Character.toUpperCase(nameNoUnderscores.charAt(0)) + nameNoUnderscores.substring(1);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.classnameformat") + " \"" + newName + '"';
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.classnameformat") + " \"" + newName + '"';
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

        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
            return;
        }
        try {
            final RenameProcessor processor = new RenameProcessor(startElement.getProject(),
                    startElement.getParent(), newName, false, false);
            for (AutomaticRenamerFactory factory : Extensions.getExtensions(AutomaticRenamerFactory.EP_NAME)) {
                processor.addRenamerFactory(factory);
            }
            processor.run();
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}