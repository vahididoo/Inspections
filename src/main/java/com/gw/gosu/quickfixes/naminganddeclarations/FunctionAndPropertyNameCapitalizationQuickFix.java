
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
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.invoke.MethodHandles;
import static java.lang.Character.isLetter;

public class FunctionAndPropertyNameCapitalizationQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    private final boolean toUppercase;
    private final IGosuMethod method;

    public FunctionAndPropertyNameCapitalizationQuickFix(final PsiElement methodIdentifier,
                                                         final IGosuMethod method,
                                                         boolean toUppercase) {
        super(methodIdentifier);
        this.toUppercase = toUppercase;
        this.method = method;
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.functionandpropertynamecapitalization");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "Function Name Lowercase 2014";
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
            StringBuilder reformattedNameBuilder = new StringBuilder(this.method.getName());

            if (!Character.isAlphabetic(reformattedNameBuilder.charAt(0))) {
                reformattedNameBuilder.insert(0, "a");
            }

            reformattedNameBuilder.setCharAt(0, this.getFirstCharacter(this.method.getName()));

            //API Call used to rename all associated function names of the code
            final RenameProcessor processor = new RenameProcessor(
                    startElement.getProject(),
                    startElement.getParent(),
                    reformattedNameBuilder.toString(),
                    false,
                    false);

            for (AutomaticRenamerFactory factory : Extensions.getExtensions(AutomaticRenamerFactory.EP_NAME)) {
                processor.addRenamerFactory(factory);
            }

            processor.run();
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    private char getFirstCharacter(String methodName) {
        return this.toUppercase
            ? Character.toUpperCase(methodName.charAt(0))
            : Character.toLowerCase(methodName.charAt(0));
    }
}