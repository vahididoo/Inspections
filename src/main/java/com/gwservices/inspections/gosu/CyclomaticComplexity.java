package com.gwservices.inspections.gosu;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.tree.IElementType;
import gw.gosu.ij.psi.GosuElementVisitor;
import gw.gosu.ij.psi.impl.source.tree.GosuElementTypes;
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

    private static final Map<IElementType, Factor> factorsMap = new HashMap<>();

    static {
        factorsMap.put(GosuElementTypes.RETURN_STATEMENT, new Factor(GosuElementTypes.RETURN_STATEMENT, false));
        factorsMap.put(GosuElementTypes.ANDAND, new Factor(GosuElementTypes.ANDAND, false));
        factorsMap.put(GosuElementTypes.OROR, new Factor(GosuElementTypes.OROR, false));
        factorsMap.put(GosuElementTypes.QUEST, new Factor(GosuElementTypes.QUEST, false, 2));
        factorsMap.put(GosuElementTypes.CASE_KEYWORD, new Factor(GosuElementTypes.CASE_KEYWORD, false));
        factorsMap.put(GosuElementTypes.IF_STATEMENT, new Factor(GosuElementTypes.IF_STATEMENT, false));
        factorsMap.put(GosuElementTypes.DO_WHILE_STATEMENT, new Factor(GosuElementTypes.DO_WHILE_STATEMENT, false));
        factorsMap.put(GosuElementTypes.WHILE_STATEMENT, new Factor(GosuElementTypes.WHILE_STATEMENT, false));
        factorsMap.put(GosuElementTypes.CATCH_SECTION, new Factor(GosuElementTypes.CATCH_SECTION, false));
        factorsMap.put(GosuElementTypes.THROW_STATEMENT, new Factor(GosuElementTypes.THROW_STATEMENT, false));
        factorsMap.put(GosuElementTypes.FINALLY_KEYWORD, new Factor(GosuElementTypes.FINALLY_KEYWORD, false));
    }

    public int threshold = 7;



    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {

        return new GosuElementVisitor() {



            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                int complexity = 1;
                if (method.getBody() == null) {
                    return;
                }

                complexity = processElements(method.getChildren(), complexity);

                if (method.getReturnType() != null && !method.getReturnType().equals(GosuElementTypes.VOID_KEYWORD)) {
                    complexity--;
                }

                if (complexity > threshold) {
                    holder.registerProblem(method.getNameIdentifier(), "Cyclomatic complexity " + complexity + " exceeds " + threshold, ProblemHighlightType.INFORMATION);
                }
            }


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
        private final IElementType elementType;
        private boolean needsBlock;
        private int factorCount;

        public Factor(IElementType elementType, boolean needsBlock) {
            this(elementType, needsBlock, 1);

        }

        public Factor(IElementType elementType, boolean needsBlock, int factorCount) {
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
