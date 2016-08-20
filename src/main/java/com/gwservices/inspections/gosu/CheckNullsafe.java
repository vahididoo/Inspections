package com.gwservices.inspections.gosu;

import com.gwservices.inspections.util.*;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.dataFlow.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.psi.*;
import gw.gosu.ij.psi.*;
import gw.gosu.ij.psi.impl.source.tree.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/1/2016.
 */
public class CheckNullsafe extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        super.buildVisitor(holder, isOnTheFly);

        return new GosuElementVisitor() {
  /*          @Override
            public void visitMethod(PsiMethod method) {
                if (!isOnTheFly) {
                    return;
                }
                super.visitMethod(method);
                if (GosuNullityInference.inferNullity(method).equals(Nullness.NULLABLE)) {
                    holder.registerProblem(method, "Method could return null");
                }
            }*/

            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);

                if (expression.getQualifier() != null) {
                    if (expression.getQualifier() instanceof PsiMethodCallExpression) {
                        PsiReferenceExpression methodExpression = ((PsiMethodCallExpression) expression.getQualifier
                                ()).getMethodExpression();
                        PsiMethod resolved = (PsiMethod) methodExpression.resolve();
                        if (resolved != null) {
                            Nullness nullness = GosuNullityInference.inferNullity(resolved);
                            checkNullSafe(expression, resolved, nullness, holder);
                        }
                    } else if (expression.getQualifier() instanceof PsiReferenceExpression) {
                        PsiElement resolved = ((PsiReferenceExpression) expression.getQualifier()).resolve();
                        if (resolved instanceof PsiLocalVariable) {
                            Nullness nullness = GosuNullityInference.inferNullity((PsiLocalVariable) resolved);
                            checkNullSafe(expression, resolved, nullness, holder);
                        }
                    }

                }
            }
        };
    }

    private void checkNullSafe(PsiReferenceExpression expression, PsiElement resolvedMethod, Nullness nullness,
                               @NotNull ProblemsHolder holder) {

        if (nullness.equals(Nullness.NULLABLE) && expression.getQualifier() != null) {
            for (PsiElement psiElement : expression.getChildren()) {
                if (psiElement.getReference() != null && psiElement.getReference().isReferenceTo(resolvedMethod)) {
                    if (psiElement.getNextSibling() != null && psiElement.getNextSibling().getNode() != null) {
                        if (GosuElementTypes.QUESTDOT.equals(psiElement.getNextSibling().getNode().getElementType())) {
                            return;
                        }
                    }
                }

            }
            if (!checkSurroundingExpression(expression, resolvedMethod)) {
                holder.registerProblem(expression, "Expression will throw NPE. Check for null.", ProblemHighlightType
                        .GENERIC_ERROR);
            }

        } else if (nullness.equals(Nullness.UNKNOWN)) {
            holder.registerProblem(expression, "Expression has the potential to throw NPE. Check " + "" + "" + "" +
                    "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "" +
                    "for " + "" + "" + "" + "" + "null" + "" + ".", ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
        }
    }

    private boolean checkSurroundingExpression(PsiElement expression, PsiElement resolvedMethod) {
        if (expression.getParent() == null) {
            return false;
        }
        PsiElement enclosingStatement = expression.getParent();
        if (enclosingStatement instanceof PsiIfStatement && ((PsiIfStatement) enclosingStatement).getCondition()
                instanceof PsiBinaryExpression) {
            PsiBinaryExpression condition = (PsiBinaryExpression) ((PsiIfStatement) enclosingStatement).getCondition();
            if (condition.getROperand().getText().equalsIgnoreCase("null")) {
                PsiExpression lOperand = condition.getLOperand();
                if (lOperand instanceof PsiReferenceExpression) {
                    if (((PsiReferenceExpression) lOperand).isReferenceTo(resolvedMethod)) {
                        return true;
                    } else {
                        return checkSurroundingExpression(enclosingStatement, resolvedMethod);
                    }
                } else if (lOperand instanceof PsiMethodCallExpression) {
                    if (!((PsiMethodCallExpression) lOperand).getMethodExpression().resolve().equals(resolvedMethod)) {
                        return checkSurroundingExpression(expression, resolvedMethod);
                    }
                    return true;
                }
            }
        } else if (enclosingStatement instanceof PsiDeclarationStatement) {
            return checkSurroundingExpression(enclosingStatement, resolvedMethod);
        } else if (enclosingStatement instanceof PsiMethod || enclosingStatement instanceof PsiClass) {
            return false;
        }
        return checkSurroundingExpression(enclosingStatement, resolvedMethod);
    }
}
