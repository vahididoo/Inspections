package com.gwservices.inspections.gosu.query;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import gw.gosu.ij.psi.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/17/2016.
 */
public abstract class BaseSelectChecker extends BaseLocalInspectionTool {

    public static final String GW_API_QUERY_IQUERY_BEAN_RESULT = "gw.api.database.IQueryBeanResult<T>";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                String countWhere;
                countWhere = getInterestingKeyword();
                checkSelectStatement(expression, countWhere, holder);
            }
        };
    }

    @NotNull
    protected abstract String getInterestingKeyword();

    private void checkSelectStatement(PsiMethodCallExpression expression, String interestingKeyWord, @NotNull ProblemsHolder holder) {
        if (expression != null) {
            PsiReferenceExpression methodExpression = expression.getMethodExpression();
            if (methodExpression != null && methodExpression.getReference() != null) {
                PsiElement resolvedType = methodExpression.getReference().resolve();
                if (resolvedType != null && resolvedType instanceof PsiMethod) {
                    PsiType returnType = ((PsiMethod) resolvedType).getReturnType();
                    if (returnType != null && returnType instanceof PsiClassReferenceType && ((PsiClassReferenceType) returnType).getReference().getCanonicalText().equalsIgnoreCase(GW_API_QUERY_IQUERY_BEAN_RESULT)) {

                        if (methodExpression.getParent() != null
                                && methodExpression.getParent().getParent() != null
                                //TODO check for an array of interesting keywords
                                && interestingKeyWord.equalsIgnoreCase(((PsiReferenceExpression) methodExpression.getParent().getParent()).getReferenceName())) {
                            holder.registerProblem(methodExpression.getParent().getParent().getReference().getElement(), "Consider narrowing the selected statement instead of using  " + interestingKeyWord);
                        }
                    }
                }
            }
        }
    }
}
