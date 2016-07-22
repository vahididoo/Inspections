
package com.gw.gosu.quickfixes.naminganddeclarations;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * Fixes public fields by transforming them into a property backed by private variable.
 */
public class PublicVarAsPropertyQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    /** Logger. */
    private static final Logger LOG =
            Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     * @param elem The entire variable element
     */
    public PublicVarAsPropertyQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.publicvarasproperty");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.publicvarasproperty");
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
            StringBuilder propertyStringBuilder = new StringBuilder();
            PsiElement current = startElement.getFirstChild();

            PsiElement modifier = current.getFirstChild();

            //
            //Skip all the modifiers and change 'public' to 'private'
            //
            while (modifier != null) {
                if (Common.isElementType(modifier, GosuElementTypes.TT_public)) {
                    propertyStringBuilder.append(GosuElementTypes.TT_private);
                }
                else {
                    propertyStringBuilder.append(modifier.getText());
                }
                modifier = modifier.getNextSibling();
            }

            current = current.getNextSibling(); //Skip past the variable modifiers

            while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
            {
                propertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            propertyStringBuilder.append(current.getText());
            current = current.getNextSibling(); //Skip over the var keyword

            while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
            {
                propertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            String varName = current.getText();

            current = current.getNextSibling(); //Skip over the identifier

            //We need a second string builder so we can find out if we need a new variable name
            StringBuilder remainingPropertyStringBuilder = new StringBuilder();

            while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
            {
                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            remainingPropertyStringBuilder.append(current.getText());
            current = current.getNextSibling(); //Skip over the ':' character

            while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
            {
                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            remainingPropertyStringBuilder.append(current.getText());
            current = current.getNextSibling(); //Skip over the type

            while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
            {
                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            //
            // Before checking for an assignment part, add the 'as' declaration if it doesn't exist
            //
            if (!Common.isElementType(current, GosuElementTypes.TT_as)) {
                //Create a new variable with 3 underscores before it (so that it's unlikely to cause a conflict with an existing variable)
                propertyStringBuilder.append("___");
                propertyStringBuilder.append(varName);
                remainingPropertyStringBuilder.append(' ');
                remainingPropertyStringBuilder.append(GosuElementTypes.TT_as);
                remainingPropertyStringBuilder.append(' ');
                remainingPropertyStringBuilder.append(varName);
                remainingPropertyStringBuilder.append(' ');
            } else {
                //Add in the variable name because we know a new one doesn't need to be created
                propertyStringBuilder.append(varName);

                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling(); //Skip over the 'as'


                while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
                {
                    remainingPropertyStringBuilder.append(current.getText());
                    current = current.getNextSibling();
                }

                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling(); //Skip over the variable identifier

                while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
                {
                    remainingPropertyStringBuilder.append(current.getText());
                    current = current.getNextSibling();
                }
            }

            //
            //Check if there is an assignment part
            //
            if (Common.isElementType(current, GosuElementTypes.TT_OP_assign)) {
                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling(); //Skip over the equals part

                while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) //Skip over whitespace and comments
                {
                    remainingPropertyStringBuilder.append(current.getText());
                    current = current.getNextSibling();
                }

                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            //Make sure all the remaining text is added so we don't lose anything like comments
            while (current != null) {
                remainingPropertyStringBuilder.append(current.getText());
                current = current.getNextSibling();
            }

            //Replace whole line with the new statement
            file.getViewProvider().getDocument().replaceString(startElement.getFirstChild().getTextOffset(),
                    startElement.getLastChild().getTextOffset() + startElement.getLastChild().getTextLength(),
                    propertyStringBuilder + remainingPropertyStringBuilder.toString());

        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}