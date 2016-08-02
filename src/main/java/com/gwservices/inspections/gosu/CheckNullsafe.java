package com.gwservices.inspections.gosu;

import com.gwservices.inspections.util.GosuNullityInference;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.dataFlow.Nullness;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import gw.gosu.ij.psi.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 8/1/2016.
 */
public class CheckNullsafe extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        super.buildVisitor(holder, isOnTheFly);

        return new GosuElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                if (!isOnTheFly) {
                    return;
                }
                super.visitMethod(method);
                if (GosuNullityInference.inferNullity(method).equals(Nullness.NULLABLE)) {
                    holder.registerProblem(method, "Method could return null");
                }
            }
        };
    }
}
