package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import gw.lang.parser.expressions.IEqualityExpression;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuFieldAccessExpressionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/22/2016.
 */
public class ActivityPatternComparison extends BaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitFieldAccessExpression(GosuFieldAccessExpressionImpl expression) {
                super.visitFieldAccessExpression(expression);

                if (expression != null) {
                    if ("ActivityPattern".equalsIgnoreCase(expression.getReferenceName())) {
                        if (expression.getParent().getNode().getElementType().equals(GosuElementTypes.ELEM_TYPE_EqualityExpression)) {
                            holder.registerProblem(expression, "Reference Name: " + ((IEqualityExpression) expression.getParent()).getRHS().getType());
                        }
                    }

                }
            }

        };
    }


}



