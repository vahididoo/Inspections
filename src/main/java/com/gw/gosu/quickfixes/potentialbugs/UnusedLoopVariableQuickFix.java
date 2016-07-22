
package com.gw.gosu.quickfixes.potentialbugs;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.CommentExtractor;
import com.gw.gosu.utility.Common;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * Removes the unused variable from the for each loop.
 */
public class UnusedLoopVariableQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    /** Logger. */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     * @param elem The foreach loop
     */
    public UnusedLoopVariableQuickFix(final PsiElement elem) {
        super(elem);
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("potentialbugs.fix.tooltip.unusedloopvariable");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("potentialbugs.fix.tooltip.unusedloopvariable");
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
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
        if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
            return;
        }
        try {

            CommentExtractor comments = new CommentExtractor(Common.getStartOfLineIndent(startElement));
            startElement = startElement.getFirstChild();

            //Get to either the 'var' keyword or the variable first (The var keyword isn't required)
            while (!Common.isElementType(startElement, GosuElementTypes.TT_var)
                    && !(startElement instanceof PsiVariable)) {
                startElement = startElement.getNextSibling();
            }

            endElement = startElement;

            while (!Common.isElementType(endElement, GosuElementTypes.TT_in)) {
            //Skip over everything until the 'in' keyword is reached
                //Save any comments that are deleted
                comments.extractComments(endElement);
                endElement = endElement.getNextSibling();
            }

            endElement = endElement.getNextSibling(); //Skip over the 'in' keyword

            while (endElement instanceof PsiWhiteSpace) { //Skip over whitespace ONLY (We don't want to delete comments
                endElement = endElement.getNextSibling();
            }


            file.getViewProvider().getDocument().replaceString(startElement.getTextOffset(),
                    endElement.getTextOffset(),
                    //The end element is one we want to keep so only give the offset from the start
                    comments.toString());
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}