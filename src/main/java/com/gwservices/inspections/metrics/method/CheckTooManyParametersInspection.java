package com.gwservices.inspections.metrics.method;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * Created by vmansoori on 8/17/2016.
 */
public class CheckTooManyParametersInspection extends BaseParametrizedInspectionTool {

    public int threshold = 7;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                int numParams = method.getParameterList().getParameters().length;
                if (numParams > threshold) {
                    holder.registerProblem(method, "Number of parameters " + numParams + " exceeds " + threshold + " "
                            + "" + "" + "" + "" + "" + "for <code>#ref</code>.", ProblemHighlightType.INFORMATION);
                }
            }
        };
    }

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        return getSingleIntegerOptionsPanel("Maximum acceptable coupling", "threshold");
    }
}
