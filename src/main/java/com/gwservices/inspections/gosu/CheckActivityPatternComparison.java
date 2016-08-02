package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiReferenceExpression;
import gw.gosu.ij.psi.GosuElementVisitor;
import gw.gosu.ij.psi.GosuPsiNewExpression;
import gw.gosu.ij.psi.impl.source.tree.GosuElementTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/22/2016.
 */
public class CheckActivityPatternComparison extends BaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                PsiExpression rOperand = expression.getROperand();
                if (rOperand.getNode().getElementType().equals(GosuElementTypes.NEW_EXPRESSION)) {
                    if (((GosuPsiNewExpression) rOperand).getClassReference().getReferenceName().equalsIgnoreCase("ActivityPattern")) {
                        PsiExpression lOperand = expression.getLOperand();
                        if (lOperand instanceof PsiReferenceExpression) {
                            if (((PsiReferenceExpression) lOperand).getReferenceName().equalsIgnoreCase("ActivityPattern")) {
                                {
                                    holder.registerProblem(expression, "This type of Activity comparison is not performant", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                }
                            }
                        }
                    }
                }
            }
        };
    }
}



