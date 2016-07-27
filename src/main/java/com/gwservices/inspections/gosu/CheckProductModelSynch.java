package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuBeanMethodCallExpressionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmansoori on 7/26/2016.
 */
public class CheckProductModelSynch extends BaseLocalInspectionTool {
    private static final List<String> methodNames = new ArrayList<>();

    static {
        methodNames.add("syncQuestions");
        methodNames.add("syncPolicyLines");
        methodNames.add("syncPolicyTerm");
        methodNames.add("syncCoverables");
        methodNames.add("syncModifiers");
//        methodNames.add("applyOfferingForExclusionCovTerms");

    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {


        return new GosuElementVisitor() {

            @Override
            public void visitBeanMethodCallExpression(GosuBeanMethodCallExpressionImpl callExpression) {
                super.visitBeanMethodCallExpression(callExpression);

                PsiElement ctx = callExpression.getContext() != null ? callExpression.getContext() : callExpression.getQualifier();
                if (callExpression.getQualifier() != null && methodNames.contains((callExpression.getCanonicalText()))) {
                    holder.registerProblem(callExpression, "Call to synchronize product model <code>" + callExpression.getCanonicalText() + "</code>", ProblemHighlightType.WEAK_WARNING);
                }
            }


        };
    }
}
