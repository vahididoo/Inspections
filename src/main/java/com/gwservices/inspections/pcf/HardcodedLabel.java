package com.gwservices.inspections.pcf;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/18/2016.
 */
public class HardcodedLabel extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if ("pcf".equalsIgnoreCase(element.getContainingFile().getVirtualFile().getExtension())) {
                    if (element instanceof XmlAttribute) {
                        if ("label".equalsIgnoreCase(((XmlAttribute) element).getLocalName())) {
                            if (((XmlAttribute) element).getValue().startsWith("&quot;")) {
                                holder.registerProblem(element.getParent(), "Hardcoded lables <code>" + ((XmlAttribute) element).getName() + "</code>");
                            }
                        }
                    }
                }
            }
        };
    }
}
