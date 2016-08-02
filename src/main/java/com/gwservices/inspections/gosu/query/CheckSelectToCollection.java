package com.gwservices.inspections.gosu.query;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import gw.gosu.ij.psi.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/17/2016.
 */
public class CheckSelectToCollection extends BaseSelectChecker {


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {


            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                PsiReferenceExpression methodExpression = expression.getMethodExpression();
                if (isOfInterest(expression.getText())) {
                    if (methodExpression.getQualifier() != null && "Select".equalsIgnoreCase(methodExpression.getQualifier().getText())) {
                        holder.registerProblem(methodExpression.getQualifier(), "Use of Select.ToCollection");
                    }
                }

            }


            private boolean isOfInterest(String referenceName) {
                return referenceName != null && ("tocollection".equalsIgnoreCase(referenceName) || "toset".equalsIgnoreCase(referenceName)
                        || "tolist".equalsIgnoreCase(referenceName));
            }


        };
    }

    @NotNull
    @Override
    protected String getInterestingKeyword() {
        return "tocollection";
    }
}