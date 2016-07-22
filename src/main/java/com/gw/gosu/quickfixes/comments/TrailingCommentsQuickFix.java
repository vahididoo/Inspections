
package com.gw.gosu.quickfixes.comments;

import com.gw.gosu.inspections.SampleBundle;
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
import java.util.List;

/**
 * Aligns all given trailing comments to the same indent as the maximum indent of the given list.
 */
public class TrailingCommentsQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    /** Logger. */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());


    /**
     * The maximum indent within the group of trailing comments that this belongs to.
     */
    private int maxIndent;
    /**
     * The list of trailing comments.
     */
    private List<PsiElement> elems;

    /**
     * Constructor.
     * @param elemList The list of trailing comments
     * @param maxindent The maximum indent within the group of trailing comments that this belongs to
     */
    public TrailingCommentsQuickFix(final List<PsiElement> elemList, final int maxindent) {
        super(elemList.get(0));
        this.elems = elemList;
        this.maxIndent = maxindent;
    }


    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("comments.fix.tooltip.trailingcomments");
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return SampleBundle.message("comments.fix.tooltip.trailingcomments");
    }

    @Override
    public final void invoke(@NotNull final Project project,
                       @NotNull final PsiFile file,
                       @Nullable("is null when called from inspection") final Editor editor,
                       @NotNull final PsiElement startElement,
                       @NotNull final PsiElement endElement) {

        //Have a total diff that records how many characters have been added so far
        //This serves as an offset for the replace string function (This is required because the offset of the elements
        //doesn't get updated with each replacement
        int totalDiff = 0;


        //Firstly, find all the comments that arent aligned correctly
        for (int i = 0; i < elems.size(); i++) {
            int diff = maxIndent - Common.getIndent(elems.get(i)); // get the diff distance between max and each one

            if (diff > 0) {
                StringBuilder whitespace = new StringBuilder();
                for (int ws = 0; ws < diff; ws++) {
                    whitespace.append(' ');
                }

                if (!CodeInsightUtilBase.prepareFileForWrite(startElement.getContainingFile())) {
                    return;
                }
                try {
                    file.getViewProvider().getDocument().replaceString(elems.get(i).getTextOffset() + totalDiff,
                            elems.get(i).getTextOffset() + totalDiff + elems.get(i).getTextLength(),
                            whitespace + elems.get(i).getText());
                } catch (IncorrectOperationException e) {
                    LOG.error(e);
                }

                totalDiff += diff;
            }
        }

    }
}
