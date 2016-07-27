package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.GosuElementType;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vmansoori on 7/19/2016.
 */
public class CyclomaticComplexity extends BaseLocalInspectionTool {

    private static final Map<GosuElementType, Factor> factorsMap = new HashMap<>();

    static {
        factorsMap.put(GosuElementTypes.ELEM_TYPE_ReturnStatement, new Factor(GosuElementTypes.ELEM_TYPE_ReturnStatement, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_ConditionalAndExpression, new Factor(GosuElementTypes.ELEM_TYPE_ConditionalAndExpression, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_ConditionalOrExpression, new Factor(GosuElementTypes.ELEM_TYPE_ConditionalOrExpression, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_ConditionalTernaryExpression, new Factor(GosuElementTypes.ELEM_TYPE_ConditionalTernaryExpression, false, 2));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_ReturnStatement, new Factor(GosuElementTypes.ELEM_TYPE_ReturnStatement, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_CaseClause, new Factor(GosuElementTypes.ELEM_TYPE_CaseClause, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_IfStatement, new Factor(GosuElementTypes.ELEM_TYPE_IfStatement, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_DoWhileStatement, new Factor(GosuElementTypes.ELEM_TYPE_DoWhileStatement, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_WhileStatement, new Factor(GosuElementTypes.ELEM_TYPE_WhileStatement, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_CatchClause, new Factor(GosuElementTypes.ELEM_TYPE_CatchClause, false));
        factorsMap.put(GosuElementTypes.ELEM_TYPE_ThrowStatement, new Factor(GosuElementTypes.ELEM_TYPE_ThrowStatement, false));
        factorsMap.put(GosuElementTypes.TT_finally, new Factor(GosuElementTypes.TT_finally, false));
    }

    public int threshold = 7;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {

        return new GosuElementVisitor() {
            @Override
            public void visitMethod(IGosuMethod method) {
                super.visitMethod(method);
                int complexity = 1;
                if (method.getBody() == null) {
                    return;
                }

                complexity = processElements(method.getChildren(), complexity);

                if (method.getReturnType() != null && !method.getReturnType().equals(GosuElementTypes.TT_void)) {
                    complexity--;
                }

                if (complexity > threshold) {
                    holder.registerProblem(method.getNameIdentifier(), "Cyclomatic complexity " + complexity + " exceeds " + threshold);
                }
                Logger.getInstance(this.getClass()).info("Complexity factor:" + complexity);
                System.out.printf("Class: " + method.getContainingClass().getName() + ", Method :" + method.getName() + ", Complexity:" + complexity + "\n");
            }
/*

            private int processElements(PsiElement[] children, int complexity) {

                for (PsiElement psiElement : children) {
                    if (psiElement.getNode().getElementType().equals(GosuElementTypes.ELEM_TYPE_SwitchStatement)
                            || psiElement.getNode().getElementType().equals(GosuElementTypes.ELEM_TYPE_CaseClause)) {
                        complexity = processElements(psiElement.getChildren(), complexity);
                    }
                    if (isOfInterest(psiElement)) {
                        complexity++;
                        if (psiElement.getChildren() != null) {
                            complexity = processElements(psiElement.getChildren(), complexity);
                        }
                    } else if (psiElement instanceof PsiStatement) {
                        complexity = processStatement((PsiStatement) psiElement, complexity);
                    }
                }
                return complexity;
            }

            private boolean isOfInterest(PsiElement psiElement) {

                return elementTypes.contains(psiElement.getNode().getElementType());
            }

            private int processStatement(PsiStatement psiStatement, int complexity) {
                if (psiStatement instanceof GosuIfStatementImpl ||
                        psiStatement instanceof GosuDoWhileStatementImpl ||
                        psiStatement instanceof GosuWhileStatementImpl) {
                    List<PsiCodeBlock> codeBlocks = getPsiCodeBlock(psiStatement);
                    for (PsiCodeBlock codeBlock : codeBlocks) {
                        complexity = processElements(codeBlock.getChildren(), ++complexity);
                    }
                }
                return complexity;
            }


            private List<PsiCodeBlock> getPsiCodeBlock(PsiStatement psiStatement) {
                List<PsiCodeBlock> codeBlocks = new ArrayList<>();
                for (PsiElement psiElement : psiStatement.getChildren()) {
                    if (psiElement instanceof PsiCodeBlock) {
                        codeBlocks.add((PsiCodeBlock) psiElement);
                    }
                }
                return codeBlocks;
            }
*/

            private int processElements(PsiElement[] elements, int complexity) {
                for (PsiElement psiElement : elements) {
                    Factor factor = factorsMap.get(psiElement.getNode().getElementType());
                    if (factor != null) {
                        complexity += factor.getFactors(psiElement);
                    }
                    if (psiElement.getChildren() != null && psiElement.getChildren().length > 0) {
                        complexity = processElements(psiElement.getChildren(), complexity);
                    }
                }
                return complexity;
            }
        };
    }


    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel label = new JLabel("Complexity threshold:");
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(threshold, 2, 200, 1));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    spinner.commitEdit();
                } catch (ParseException e1) {

                }
                threshold = (int) spinner.getModel().getValue();
            }
        });

        panel.add(label);
        panel.add(spinner);
        return panel;
    }


    private static class Factor {
        private final GosuElementType elementType;
        private boolean needsBlock;
        private int factorCount;

        public Factor(GosuElementType elementType, boolean needsBlock) {
            this(elementType, needsBlock, 1);

        }

        public Factor(GosuElementType elementType, boolean needsBlock, int factorCount) {
            this.elementType = elementType;
            this.needsBlock = needsBlock;
            this.factorCount = factorCount;

        }

        public boolean accepts(PsiElement element) {
            return element.getNode() != null && this.elementType.equals(element.getNode().getElementType());
        }

        public int getFactors(PsiElement element) {
            if (this.accepts(element)) {
                if (this.needsBlock && element.getChildren() != null)
                    for (PsiElement psiElement : element.getChildren()) {
                        if (psiElement instanceof PsiCodeBlock) {
                            return factorCount;
                        }
                        return 0;
                    }
                return factorCount;
            }
            return 0;
        }


    }
}
