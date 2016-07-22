package com.aviva.gosu.quickfix;

import com.gw.gosu.inspections.SampleBundle;
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
import gw.plugin.ij.lang.psi.api.types.IGosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuStatementListImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * Created by wli on 6/29/2015.
 */
public class CollectionWhereCountQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    /**
     * Constructor.
     *
     * @param where The body of the "where" bean method.
     */
    public CollectionWhereCountQuickFix(final PsiElement where) {
        super(where);
    }

    @NotNull
    @Override
    public final String getText() {
        return "where().Count should be replaced by hasElement()";
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "Aviva Inspection Rules";
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
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}

