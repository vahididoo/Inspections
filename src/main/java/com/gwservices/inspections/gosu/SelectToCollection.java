package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.api.types.IGosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuBeanMethodCallExpressionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vmansoori on 7/17/2016.
 */
public class SelectToCollection extends BaseLocalInspectionTool {


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitBeanMethodCallExpression(GosuBeanMethodCallExpressionImpl callExpression) {
                super.visitBeanMethodCallExpression(callExpression);
                System.out.printf(callExpression.getReferenceName() + "\n");
                if ("Where".equalsIgnoreCase(callExpression.getReferenceName())) {
                    IGosuCodeReferenceElement leftQualifier = callExpression.getQualifier();
                    if (isOfInterest(leftQualifier.getReferenceName())) {
                        IGosuCodeReferenceElement qualifier = leftQualifier.getQualifier();
                        if (qualifier != null && "select".equalsIgnoreCase(qualifier.getReferenceName()))
                            holder.registerProblem(leftQualifier, "Use of Select.ToCollection");
                    }
                }
            }

            private boolean isOfInterest(String referenceName) {
                return referenceName != null && ("tocollection".equalsIgnoreCase(referenceName) || "toset".equalsIgnoreCase(referenceName)
                        || "tolist".equalsIgnoreCase(referenceName));
            }


        };
    }
}