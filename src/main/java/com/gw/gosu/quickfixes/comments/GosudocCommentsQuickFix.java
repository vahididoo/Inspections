
package com.gw.gosu.quickfixes.comments;

import com.gw.gosu.utility.Common;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.javadoc.PsiDocCommentImpl;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuAnnotationExpressionImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuStatementListImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GosudocCommentsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    public GosudocCommentsQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        String quickfixTooltipsName = "Add in Gosudoc Comments before definitions";
        return quickfixTooltipsName;
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "Gosudoc Comments";
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

            String commentforReturn = "";
            //If there is a return, then add the '@Return' part comments -- Head
            if (startElement.getParent().getText().contains("return") && returnCondition!=0) {
                commentforReturn = '\n' + indentationFinal +
                        "@Returns" + " ( \"Your descriptions here!\" ) ";
            }
            //If there is a return, then add the '@Return' part comments -- End
            String[] breakdown = finalresult.split("\\s+");
            StringBuilder commentforParam = new StringBuilder();
            //If there is an object name & type, then add the '@Param' part comments -- Head
            for (int counter = 1; counter < breakdown.length; counter++) {
                if (!breakdown[counter].equals("\b") && !breakdown[counter].equals("\\s")) {
                    commentforParam.append('\n').append(indentationFinal).append("@Param ( \"").append(breakdown[counter])
                            .append("\", \"The data type is: ").append(breakdown[counter+ 1]).append("\" )");
                    counter++;
                }
            }
            //If there is an object name & type, then add the '@Param' part comments -- End
            StringBuilder commentsBox = new StringBuilder();
            //This is a normal block comment part of doc comment -- Head
            commentsBox.append('\n')
                    .append(indentationFinal)
                    .append("/**\n")
                    .append(indentationFinal)
                    .append(" * TODO: Please enter your comments here!\n")
                    .append(indentationFinal)
                    .append(" */");
            String commentsBoxFinal = commentsBox.toString();
            //This is a normal block comment part of doc comment -- End
            String commentforParamFinal = commentforParam.toString();
            String reformattedName = "\n"+ indentationFinal
                    + commentsBoxFinal + commentforParamFinal + commentforReturn;

            List<PsiElement> gosuCommentElements = createGosuComment(startElement.getManager(), reformattedName);

            Collections.reverse(gosuCommentElements);

            for (PsiElement element : gosuCommentElements) {
                CodeStyleManager.getInstance(startElement.getProject()).reformat(element);
                startElement.getParent().addBefore(element, startElement.getParent().getFirstChild());
            }
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }

    private List<PsiElement> createGosuComment(PsiManager psiManager, String commentText) {
        final List<PsiElement> result = new ArrayList<>();
        final GosuProgramFileImpl programFile = Common.createGosuProgramFile(commentText, psiManager);
        result.add(Common.searchForElementOfType(programFile, PsiDocCommentImpl.class));
        result.addAll(Common.searchForElementsOfType(programFile, GosuAnnotationExpressionImpl.class));

        return result;
    }
}