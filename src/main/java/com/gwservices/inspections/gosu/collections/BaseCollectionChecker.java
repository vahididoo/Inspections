package com.gwservices.inspections.gosu.collections;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import gw.gosu.ij.psi.GosuElementVisitor;
import gw.gosu.ij.psi.impl.source.tree.GosuElementTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/31/2016.
 */
public abstract class BaseCollectionChecker extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        super.buildVisitor(holder, isOnTheFly);
        return new GosuElementVisitor() {
            @Override
            public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                checkMethodCalll(expression, holder);
            }
        };
    }

    protected void checkMethodCalll(@NotNull PsiMethodCallExpression expression, @NotNull ProblemsHolder holder) {
        PsiMethod calledMethod = expression.resolveMethod();
        PsiElement qualifier = expression.getMethodExpression().getQualifier();
        if (qualifier != null) {
            if (qualifier instanceof PsiMethodCallExpression) {
                PsiMethod callerMethod = ((PsiMethodCallExpression) qualifier).resolveMethod();
                if (calledMethod != null && getCalled().equalsIgnoreCase(calledMethod.getName())) {
                    if (callerMethod != null && getCaller().equalsIgnoreCase(callerMethod.getName())) {
                        PsiElement leftQualifier = ((PsiMethodCallExpression) qualifier).getMethodExpression().getQualifier();
                        if (leftQualifier != null && leftQualifier.getReference() != null) {
                            PsiElement resolve = leftQualifier.getReference().resolve();
                            if (resolve != null) {
                                if (resolve instanceof PsiType) {
                                    if (isCollection((PsiType) resolve)) {
                                        registerProblem(expression.getContext() != null ? expression.getContext() : expression, holder);
                                    }
                                } else if (resolve instanceof PsiVariable) {
                                    PsiType type = ((PsiVariable) resolve).getType();
                                    if (isCollection(type)) {
                                        registerProblem(expression.getContext() != null ? expression.getContext() : expression, holder);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (qualifier.getParent() instanceof PsiReferenceExpression) {
                checkReferenceExpression((PsiReferenceExpression) qualifier.getParent(), qualifier.getContext(), holder);
            }
        }
    }

    private void registerProblem(@NotNull PsiElement expression, @NotNull ProblemsHolder holder) {
        holder.registerProblem(expression, "Use of " + getCaller() + "(...)." + getCalled() + "().Use " + getSuggestion() + " instead", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
    }


    protected void checkReferenceExpression(PsiReferenceExpression expression, ProblemsHolder holder) {
        checkReferenceExpression(expression, expression.getContext(), holder);
    }

    protected void checkReferenceExpression(PsiReferenceExpression expression, PsiElement highlightedElement, ProblemsHolder holder) {
        PsiElement element = findLastElementAfterDot(expression);
        if (element != null) {
            if (getCalled().equalsIgnoreCase(element.getText())) {
                PsiElement firstChild = expression.getFirstChild();
                if (firstChild instanceof PsiMethodCallExpression) {
                    PsiElement resolve = ((PsiMethodCallExpression) firstChild).getMethodExpression().getReference().resolve();
                    if (resolve != null && resolve instanceof PsiMethod && (getCaller().equalsIgnoreCase(((PsiMethod) resolve).getName()) && isCollection(((PsiMethod) resolve).getReturnType()))) {
                        registerProblem(highlightedElement, holder);
                    }
                } else if (firstChild instanceof PsiReferenceExpression) {
                    PsiType psiType = ((PsiReferenceExpression) firstChild).getType();
                    if (getCaller().equalsIgnoreCase(((PsiReferenceExpression) firstChild).getQualifiedName())) {
                        if (isCollection(psiType)) {
                            registerProblem(highlightedElement, holder);
                        }
                    }
                }
            }
        }
    }

    private boolean isCollection(PsiType returnType) {
        return InheritanceUtil.isInheritor(returnType, "java.util.Collection");
    }

    protected abstract String getSuggestion();


    protected PsiElement findLastElementAfterDot(@NotNull PsiReferenceExpression expression) {
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


    protected void checkComparisonWith1or0(@NotNull PsiBinaryExpression expression, ProblemsHolder holder) {
        PsiExpression rOperand = expression.getROperand();
        if (rOperand != null) {
            if (rOperand.getType() != null && rOperand.getType().equalsToText("int")) {
                if (rOperand.getText() != null && (rOperand.getText().equals("1") && (expression.getOperationTokenType().equals(GosuElementTypes.EQEQ) || expression.getOperationTokenType().equals(GosuElementTypes.LT)))
                        || (rOperand.getText().equals("0") && (expression.getOperationTokenType().equals(GosuElementTypes.EQEQ) || expression.getOperationTokenType().equals(GosuElementTypes.GT)))) {
                    PsiElement lOperand = expression.getLOperand();
                    if (lOperand.getNode() != null) {
                        if (lOperand.getNode().getElementType().equals(GosuElementTypes.METHOD_CALL_EXPRESSION)) {
                            checkMethodCalll((PsiMethodCallExpression) lOperand, holder);
                        } else if (lOperand.getNode().getElementType().equals(GosuElementTypes.REFERENCE_EXPRESSION)) {
                            checkReferenceExpression((PsiReferenceExpression) lOperand, holder);
                        }
                    }
                }
            }
        }
    }

    protected abstract String getCalled();

    protected abstract String getCaller();


}
