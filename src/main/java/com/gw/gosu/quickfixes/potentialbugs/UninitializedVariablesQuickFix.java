
package com.gw.gosu.quickfixes.potentialbugs;

import com.gw.gosu.inspections.SampleBundle;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.types.IGosuTypeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * QuickFix:
 * 1. Primitive Data Types: Give it a value straightway
 * 2. String
 * 3. Collection types
 * 4. Class & no interface
 */
public class UninitializedVariablesQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private final Logger LOG = Logger.getInstance('#' + MethodHandles.lookup().lookupClass().getName());

    private final String _varName;

    public UninitializedVariablesQuickFix(final PsiElement elem) {
        super(elem);
        _varName = elem.getText();
    }

    @NotNull
    @Override
    public final String getText() {
        return SampleBundle.message("potentialbugs.fix.tooltip.uninitializedvariables", _varName);
    }

    @Override
    @NotNull
    public final String getFamilyName() {
        return "UninitializedVariablesInspection";
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
            PsiManager psiManager = PsiManager.getInstance(project);
            GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);

            String initializationExpr;
            IGosuVariable variable = (IGosuVariable) startElement.getParent();
            PsiType varType = variable.getType();
            IGosuTypeElement dataType = variable.getTypeElementGosu();

            //Match the data type to a particular quickfix
            if ((varType == PsiType.INT)
                    || varType == PsiType.LONG
                    || varType == PsiType.SHORT
                    || varType == PsiType.BYTE) {
                // Integer types get a zero
                initializationExpr = " = 0";
            } else if (varType == PsiType.DOUBLE
                    || varType == PsiType.FLOAT) {
                // Floating-point types get a 0.0
                initializationExpr = " = 0.0";
            } else if (varType == PsiType.getJavaLangString(psiManager, projectScope)) {
                // Strings are null
                initializationExpr = " = null";
            } else if (varType == PsiType.BOOLEAN) {
                // Booleans are false
                initializationExpr = " = false";
            } else if (varType == PsiType.CHAR) {
                // Char is an unusual one - just repeat the default
                initializationExpr = " = 0 as char";
            } else {
                PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
                PsiClassType collection = elementFactory.createTypeByFQClassName("java.util.Collection");
                PsiClassType map = elementFactory.createTypeByFQClassName("java.util.Map");
                if (collection.isAssignableFrom(varType)
                        || map.isAssignableFrom(varType)
                        || (varType.getArrayDimensions() > 0)) {
                    initializationExpr = " = {}";
                } else {
                    // TODO: We're assuming this is an object type with a no-arg constructor
                    // TODO: Use a constructor prompt for the type
                    initializationExpr = " = new " + dataType.getText()+ "()";
                    // TODO: If there really is a no-arg constructor, we can omit the type declaration
                }
            }
            int offset = dataType.getTextOffset() + dataType.getTextLength();
            file.getViewProvider().getDocument().insertString(offset,
                    initializationExpr);
        } catch (IncorrectOperationException e) {
            LOG.error(e);
        }
    }
}