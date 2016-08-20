package com.gwservices.inspections.metrics.method;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * Created by vmansoori on 8/17/2016.
 */
public class MethodCouplingInspection extends BaseParametrizedInspectionTool {

    public int threshold = 7;

    private class MethodCouplingVisitor extends GosuRecursiveElementWalkingVisitor {

        private int numDependencies;

        public int getNumDependencies() {
            return numDependencies;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            // note: no call to super
            if (method.getNameIdentifier() == null) {
                return;
            }
            final CouplingVisitor visitor = new CouplingVisitor(method, false, true);
            method.accept(visitor);
            numDependencies = visitor.getNumDependencies();

            if (numDependencies <= 10) {
                return;
            }

        }
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                final MethodCouplingVisitor methodCouplingVisitor = new MethodCouplingVisitor();
                methodCouplingVisitor.visitMethod(method);
                int numDependencies = methodCouplingVisitor.getNumDependencies();
                if (numDependencies > threshold) {
                    holder.registerProblem(method, "Number of coupling " + numDependencies + "exceeds the threshold "
                            + "of " + threshold, ProblemHighlightType.INFORMATION);
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
