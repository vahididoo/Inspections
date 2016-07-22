
package com.gw.gosu.quickfixes.comments;

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

import java.lang.invoke.MethodHandles;

public class JavadocCommentsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    public JavadocCommentsQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        String quickfixTooltipsName = "Add in Javadoc Comments before definitions";
        return quickfixTooltipsName;
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "Javadoc Comments";
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
            //Get current identation of the current method definition -- Head
            int numofwhitespaces = Common.getStartOfLineIndent(startElement.getParent().getPrevSibling());
            StringBuilder indentation = new StringBuilder();
            for (int counter = 0; counter < numofwhitespaces; counter++) {
                indentation.append(' ');
            }
            String indentationFinal = indentation.toString();
            //Get current identation of the current method definition -- End

            String commentforReturn;
            StringBuilder result = new StringBuilder();
            int returnCondition = 0;
            //1. Get object names and object types in the method definitions -- Head
            for (int counter = 0; counter < startElement.getParent().getText().length(); counter++) {
                if (startElement.getParent().getText().charAt(counter) == '(') {
                    for (int count = counter; count < startElement.getParent().getText().length(); count++) {
                        if (startElement.getParent().getText().charAt(count) != ')') {
                            result.append(startElement.getParent().getText().charAt(count));
                        } else if (startElement.getParent().getText().charAt(count) == ')') {
                            //2. Identifies if there is a return statement but not a message in a print statement or so
                            for (int counting = count; counting < startElement.getParent().getText().length(); counting++) {
                                if (startElement.getParent().getText().charAt(counting) != '{'
                                        && startElement.getParent().getText().charAt(counting) == ':'){
                                    returnCondition++;
                                }
                                else if (startElement.getParent().getText().charAt(counting) == '{') {
                                    counting = startElement.getParent().getText().length();
                                }
                            }
                            //2. Identifies if there is a return statement but not a message in a print statement or so -- End
                            count = startElement.getParent().getText().length();
                        }
                    }
                    counter = startElement.getParent().getText().length();
                }
            }
            String finalresult = result.toString();
            //1. Get object names and object types in the method definitions -- End

            finalresult = finalresult.replace(" ", ":");
            finalresult = finalresult.replace(",", ":");
            finalresult = finalresult.replace("(", ":");
            finalresult = finalresult.replace(")", ":");
            finalresult = finalresult.replace(":", " ");

            //If there is a return, then add the '@Return' part comments -- Head
            if (startElement.getParent().getText().contains("return") && returnCondition != 0) {
                commentforReturn = '\n' + indentationFinal +
                        " * @return" + '\n' + indentationFinal +
                        " */";
            } else {
                commentforReturn = '\n' + indentationFinal + " */";
            }
            //If there is a return, then add the '@Return' part comments -- End

            String[] breakdown = finalresult.split("\\s+");
            StringBuilder commentforParam = new StringBuilder();
            //If there is an object name & type, then add the '@Param' part comments -- Head
            //Another part is normal block comments part of doc comments
            commentforParam.append('\n')
                    .append(indentationFinal)
                    .append("/**\n")
                    .append(indentationFinal)
                    .append(" * TODO: Please enter your comments here!");
            for (int counter = 1; counter < breakdown.length; counter++) {
                if (!breakdown[counter].equals("\b") && !breakdown[counter].equals("\\s")) {
                    commentforParam.append('\n').append(indentationFinal).append(" * @param ").append(breakdown[counter]);
                    counter++;
                }
            }
            String commentforParamFinal = commentforParam.toString();
            //If there is an object name & type, then add the '@Param' part comments -- End

            String reformattedName = "\n"+ indentationFinal
                    + commentforParamFinal + commentforReturn
                    + "\n"+ indentationFinal;

            PsiElement newstartElement = startElement.getParent().getPrevSibling();
            file.getViewProvider().getDocument().replaceString(newstartElement.getTextOffset(),
                    newstartElement.getTextOffset() + newstartElement.getTextLength(),
                    reformattedName);
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}
