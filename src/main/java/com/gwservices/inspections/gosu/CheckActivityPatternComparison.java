package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import gw.gosu.ij.psi.*;
import gw.gosu.ij.psi.impl.source.tree.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 7/22/2016.
 */
public class CheckActivityPatternComparison extends BaseJavaLocalInspectionTool {

    public static final String ACTIVITY_PATTERN = "ActivityPattern";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                PsiExpression rOperand = expression.getROperand();
                if (rOperand.getNode() != null && GosuElementTypes.NEW_EXPRESSION.equals(rOperand.getNode()
                                                                                                 .getElementType())) {
                    if (((GosuPsiNewExpression) rOperand).getClassReference() != null && ACTIVITY_PATTERN
                            .equalsIgnoreCase(((GosuPsiNewExpression) rOperand).getClassReference().getReferenceName())) {
                        PsiExpression lOperand = expression.getLOperand();
                        if (lOperand instanceof PsiReferenceExpression) {
                            if (ACTIVITY_PATTERN.equalsIgnoreCase(((PsiReferenceExpression) lOperand)
                                    .getReferenceName())) {
                                {
                                    holder.registerProblem(expression, "This type of Activity comparison is not " +
                                            "performant", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                }
                            }
                        }
                    }
                }
            }
        };
    }
}



