package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReferenceExpression;
import gw.gosu.ij.psi.GosuElementVisitor;
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
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);

                if (expression.getQualifier() != null && methodNames.contains((expression.getReferenceName()))) {
                    holder.registerProblem(expression, "Call to synchronize product model <code>" + expression.getCanonicalText() + "</code>", ProblemHighlightType.WEAK_WARNING);
                }
            }
        };
    }
}
