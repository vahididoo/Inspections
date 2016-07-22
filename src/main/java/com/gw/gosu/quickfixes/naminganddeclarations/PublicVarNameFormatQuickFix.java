
package com.gw.gosu.quickfixes.naminganddeclarations;

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

import java.lang.invoke.MethodHandles;

import static java.lang.Character.isLetter;

public class PublicVarNameFormatQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    private final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    public PublicVarNameFormatQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return "Change first letter of the public var name to uppercase";
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        String quickfixTooltipsName = "Public Var Name Format 2014";
        return quickfixTooltipsName;
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
        String uppercaseFirstName = startElement.getText().toUpperCase();
        StringBuilder result = new StringBuilder();

        boolean firstCharIsLetter;
        int continuePosition = 0;
        for (int counter = 0; counter < startElement.getText().length(); counter++) {
            firstCharIsLetter = isLetter(startElement.getText().charAt(counter));
            //Search for and get first alphabetical letter instead of notations
            //And change it to lowercase if not and get it
            if (firstCharIsLetter){
                result.append(uppercaseFirstName.charAt(counter));
                continuePosition = counter;
                counter = startElement.getText().length();
            }
        }

        //Re-format public var name into an uppercase letter beginning name
        for (int counter = continuePosition+ 1; counter < startElement.getText().length(); counter++) {
            result.append(startElement.getText().charAt(counter));
        }

        String finalresult = result.toString();
        String reformattedName;
        reformattedName = finalresult;
        System.out.print("reformattedName: "+ reformattedName);
        try {
            final RenameProcessor processor = new RenameProcessor(startElement.getProject(), startElement.getParent(), reformattedName, false, false);
            for (AutomaticRenamerFactory factory : Extensions.getExtensions(AutomaticRenamerFactory.EP_NAME)) {
                processor.addRenamerFactory(factory);
            }
            processor.run();
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}