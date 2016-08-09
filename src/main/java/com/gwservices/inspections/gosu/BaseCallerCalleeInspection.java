package com.gwservices.inspections.gosu;

import com.intellij.codeInsight.*;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.psi.*;
import gw.gosu.ij.psi.*;
import gw.gosu.ij.psi.impl.source.tree.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/1/2016.
 */
public abstract class BaseCallerCalleeInspection extends BaseLocalInspectionTool {
    protected abstract boolean isCallerOfInterestedType(PsiType returnType);

    protected abstract boolean isCalledOfInterestedType(PsiType returnType);

    protected abstract boolean isCalledOfInterestedName(String calledName);

    protected abstract boolean isCallerOfInterestedName(String callerName);

    protected abstract void registerProblem(PsiElement expression, ProblemsHolder holder);

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        super.buildVisitor(holder, isOnTheFly);
        return new GosuElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                if (!CommentUtil.isComment(expression)) {
                    checkMethodCall(expression, holder);
                }

            }

            @Override
            public void visitBinaryExpression(PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);
                //TODO process binary expressions
            }

            @Override
            public void visitReferenceExpression(PsiReferenceExpression expression) {
                super.visitReferenceExpression(expression);
                if (!CommentUtil.isComment(expression)) {
                    processReferenceExpression(expression, holder);
                }

            }
        };
    }

    protected boolean isMatch(String calledName, String interestedName) {
        return calledName != null && interestedName.equalsIgnoreCase(calledName);
    }

    protected void checkMethodCall(@NotNull PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        PsiMethod calledMethod = expression.resolveMethod();
        PsiElement caller = expression.getMethodExpression().getQualifier();
        if (caller != null) {
            if (caller instanceof PsiMethodCallExpression) {
                processMethodCallExpression(expression, holder, calledMethod, (PsiMethodCallExpression) caller);
            } else if (caller.getParent() instanceof PsiReferenceExpression) {
                processReferenceExpression((PsiReferenceExpression) caller.getParent(), holder);
            }
        }
    }

    protected void checkComparisonWithOneOrZero(@NotNull PsiBinaryExpression expression, ProblemsHolder holder) {
        PsiExpression rOperand = expression.getROperand();
        if (rOperand != null) {
            if (rOperand.getType() != null && (rOperand.getType().equalsToText("int") || rOperand.getType()
                                                                                                 .equalsToText
                                                                                                         ("byte"))) {
                if (rOperand.getText() != null && (rOperand.getText().equals("1") && (expression
                        .getOperationTokenType().equals(GosuElementTypes.EQEQ) || expression.getOperationTokenType()
                                                                                            .equals(GosuElementTypes
                                                                                                    .LT))) ||
                        (rOperand.getText().equals("0") && (expression.getOperationTokenType().equals
                                (GosuElementTypes.EQEQ) || expression.getOperationTokenType().equals(GosuElementTypes
                                .GT)))) {
                    PsiElement lOperand = expression.getLOperand();
                    if (lOperand.getNode() != null) {
                        if (lOperand.getNode().getElementType().equals(GosuElementTypes.METHOD_CALL_EXPRESSION)) {
                            checkMethodCall((PsiMethodCallExpression) lOperand, holder);
                        } else if (lOperand.getNode().getElementType().equals(GosuElementTypes.REFERENCE_EXPRESSION)) {
                            processReferenceExpression((PsiReferenceExpression) lOperand, holder);
                        }
                    }
                }
            }
        }
    }

    private PsiElement findLastElementAfterDot(@NotNull PsiReferenceExpression expression) {
        PsiElement lastChild = expression.getLastChild();

        while (lastChild.getPrevSibling() != null) {
            if (lastChild != null && lastChild.getNode().getElementType().equals(GosuElementTypes.DOT)) {
                return lastChild.getNextSibling();
            } else {
                lastChild = lastChild.getPrevSibling();
            }
        }
        return lastChild;
    }

    private void processReferenceExpression(PsiReferenceExpression expression, ProblemsHolder holder) {
        PsiElement element = findLastElementAfterDot(expression);
        if (element != null) {
            if (isCalledOfInterestedName(element.getText())) {
                PsiElement firstChild = expression.getFirstChild();
                if (firstChild instanceof PsiMethodCallExpression) {
                    PsiMethod resolve = ((PsiMethodCallExpression) firstChild).resolveMethod();
                    if (resolve != null && resolve instanceof PsiMethod && (isCallerOfInterestedName(resolve.getName
                            ())) && isCallerOfInterestedType(resolve.getReturnType())) {
                        registerProblem(expression, holder);
                    }
                } else if (firstChild instanceof PsiReferenceExpression) {
                    PsiType psiType = ((PsiReferenceExpression) firstChild).getType();
                    if (isCallerOfInterestedName(((PsiReferenceExpression) firstChild).getQualifiedName()) &&
                            isCalledOfInterestedName(((PsiReferenceExpression) firstChild).getQualifiedName())) {
                        if (isCallerOfInterestedType(psiType)) {
                            registerProblem(expression, holder);
                        }
                    }
                }
            }
        }
    }

    private void processMethodCallExpression(@NotNull PsiMethodCallExpression expression, @NotNull ProblemsHolder
            holder, PsiMethod calledMethod, PsiMethodCallExpression caller) {
        PsiMethod callerMethod = caller.resolveMethod();
        if (calledMethod != null && isCalledOfInterestedName(calledMethod.getName())) {
            if (callerMethod != null && isCallerOfInterestedName(callerMethod.getName())) {
                PsiElement leftQualifier = caller.getMethodExpression().getQualifier();
                if (leftQualifier != null && leftQualifier.getReference() != null) {
                    PsiElement resolvedCaller = leftQualifier.getReference().resolve();
                    if (resolvedCaller != null) {
                        if (resolvedCaller instanceof PsiType) {
                            if (isCallerOfInterestedType((PsiType) resolvedCaller)) {
                                registerProblem(expression, holder);
                            }
                        } else if (resolvedCaller instanceof PsiVariable) {
                            PsiType type = ((PsiVariable) resolvedCaller).getType();
                            if (isCallerOfInterestedName(((PsiVariable) resolvedCaller).getName()) &&
                                    isCallerOfInterestedType(type)) {
                                registerProblem(expression, holder);
                            }
                        }
                    }
                }
            }
        }
    }
}
