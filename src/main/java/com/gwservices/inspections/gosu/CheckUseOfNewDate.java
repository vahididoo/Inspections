package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import gw.gosu.ij.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 7/18/2016.
 * todo provide quick fix.
 */
public class CheckUseOfNewDate extends BaseLocalInspectionTool {

    private static class NewDateQuickFix extends LocalQuickFixOnPsiElement {

        protected NewDateQuickFix(@NotNull PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public String getText() {
            return "Replace with <code>gw.api.util.DateUtil</code>";
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                           @NotNull PsiElement endElement) {
            if (startElement instanceof PsiNewExpression) {
                PsiType type = ((PsiNewExpression) startElement).getType();
                if (InheritanceUtil.isInheritor(type, "java.util.Date")) {
                    addImportStatement(project, startElement);
                    GosuPsiElementFactory elementFactory = GosuPsiElementFactory.SERVICE.getInstance(project);
                    PsiExpression methodCall = elementFactory.createExpressionFromText("DateUtil.currentDate()",
                            startElement);
                    startElement.replace(methodCall);

                }
            }

        }

        private void addImportStatement(@NotNull Project project, @NotNull PsiElement startElement) {
            PsiFile containingFile = startElement.getContainingFile();
            if (containingFile != null && GosuFileType.INSTANCE.equals(containingFile.getFileType())) {
                PsiImportList importList = ((PsiGosuFile) containingFile).getImportList();
                if (importList.findSingleClassImportStatement("gw.api.util.DateUtil") == null && importList
                        .findSingleImportStatement("gw.api.util.*") == null) {

                    PsiClass aClass = GosuPsiFacade.SERVICE.getInstance(project).findClass("gw.api.util" + "" + "" +
                            ".DateUtil", GlobalSearchScope.allScope(project));
                    GosuPsiElementFactory elementFactory = GosuPsiElementFactory.SERVICE.getInstance(project);
                    PsiImportStatement importStatement = elementFactory.createImportStatement(aClass);
                    importList.add(importStatement);

                }
            }
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return "Replace with <code>gw.api.util.DateUtil</code>";
        }
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {

        return new GosuElementVisitor() {

            @Override
            public void visitNewExpression(PsiNewExpression newExpression) {
                if (newExpression != null) {
                    PsiType type = newExpression.getType();
                    if (InheritanceUtil.isInheritor(type, "java.util.Date")) {
                        if (newExpression.getArgumentList() != null && newExpression.getArgumentList().getExpressions
                                () != null && newExpression.getArgumentList().getExpressions().length == 0) {
                            holder.registerProblem(newExpression, "Use or <code>#ref</code>.", ProblemHighlightType
                                    .WEAK_WARNING, new NewDateQuickFix(newExpression));
                        }
                    }
                }
            }
        };
    }
}
