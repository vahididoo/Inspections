package com.gwservices.inspections.metrics.method;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * Created by vmansoori on 7/19/2016.
 */
public class CheckMethodCyclomaticComplexity extends BaseParametrizedInspectionTool {

    public int threshold = 7;

    private class GosuCyclomaticVisitor extends GosuRecursiveElementWalkingVisitor {
        private int m_complexity = 1;

        @Override
        public void visitAnonymousClass(@NotNull PsiAnonymousClass aClass) {
            // to call to super, to keep this from drilling down
        }

        @Override
        public void visitForStatement(@NotNull PsiForStatement statement) {
            super.visitForStatement(statement);
            m_complexity++;
        }

        @Override
        public void visitForeachStatement(@NotNull PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            m_complexity++;
        }

        @Override
        public void visitIfStatement(@NotNull PsiIfStatement statement) {
            super.visitIfStatement(statement);
            m_complexity++;
        }

        @Override
        public void visitDoWhileStatement(@NotNull PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            m_complexity++;
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            m_complexity++;
        }

        @Override
        public void visitSwitchStatement(@NotNull PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            final PsiCodeBlock body = statement.getBody();
            if (body == null) {
                return;
            }
            final PsiStatement[] statements = body.getStatements();
            boolean pendingLabel = false;
            for (final PsiStatement child : statements) {
                if (child instanceof PsiSwitchLabelStatement) {
                    if (!pendingLabel) {
                        m_complexity++;
                    }
                    pendingLabel = true;
                } else {
                    pendingLabel = false;
                }
            }
        }

        @Override
        public void visitWhileStatement(@NotNull PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            m_complexity++;
        }

        @Override
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            super.visitPolyadicExpression(expression);
            final IElementType token = expression.getOperationTokenType();
            if (token.equals(JavaTokenType.ANDAND) || token.equals(JavaTokenType.OROR)) {
                m_complexity += expression.getOperands().length - 1;
            }
        }

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            super.visitCatchSection(section);
            m_complexity++;
        }

        public int getComplexity() {
            return m_complexity;
        }

        public void reset() {
            m_complexity = 1;
        }
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethod(PsiMethod method) {
                final GosuCyclomaticVisitor gosuCyclomaticVisitor = new GosuCyclomaticVisitor();
                gosuCyclomaticVisitor.visitMethod(method);
                if (gosuCyclomaticVisitor.getComplexity() > threshold) {
                    holder.registerProblem(method.getNameIdentifier(), "Method <code>" + method.getName() + "</code> "
                            + "" + "" + "" + "" + "" + "complexity of " + gosuCyclomaticVisitor.getComplexity() + " "
                            + "exceeds " + "threshold of " + threshold, ProblemHighlightType.INFORMATION);
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
