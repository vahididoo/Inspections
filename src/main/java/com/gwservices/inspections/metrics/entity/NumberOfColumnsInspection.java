package com.gwservices.inspections.metrics.entity;

import com.gwservices.inspections.metrics.method.*;
import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import net.n3.nanoxml.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

/**
 * Created by vmansoori on 8/18/2016.
 */
public class NumberOfColumnsInspection extends BaseParametrizedInspectionTool {

    public int threshold = 20;

    private static class ColumnCountXmlElementVisitor extends PsiElementVisitor {
        int columnCounter = 0;

        @Override
        public void visitElement(PsiElement element) {
            if (element instanceof XmlTag && "column".equalsIgnoreCase(((XmlTag) element).getName())) {
                columnCounter++;
            }
        }

        public int getColumnCounter() {
            return columnCounter;
        }
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {

            @Override
            public void visitXmlFile(XmlFile file) {
                super.visitXmlFile(file);
                int numCol = 0;
                ColumnCountXmlElementVisitor elementVisitor = new ColumnCountXmlElementVisitor();

                numCol = visitChildren(file, elementVisitor, numCol);

                System.out.println("Number of columns " + numCol + "\n");
                if (numCol > threshold) {
                    holder.registerProblem(file, "Number of columns of " + elementVisitor.getColumnCounter() + " " +
                            "" + "" + "exceeds the threshold of " + threshold, ProblemHighlightType.INFORMATION);
                }
            }

            private int visitChildren(XmlElement element, ColumnCountXmlElementVisitor elementVisitor, int numCol) {
                for (PsiElement psiElement : element.getChildren()) {
                    if (psiElement instanceof XMLElement) {
                        psiElement.accept(elementVisitor);
                        numCol += elementVisitor.columnCounter;
                        numCol += visitChildren((XmlElement) psiElement, elementVisitor, numCol);
                    }
                }
                return numCol;
            }

        };
    }

    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        return getSingleIntegerOptionsPanel("Max number of columns", "threshold");
    }
}
