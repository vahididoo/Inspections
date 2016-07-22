package com.gwservices.inspections.pcf;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/12/2016.
 */
public class PCFPostOnCheck extends LocalInspectionTool {


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new PsiElementVisitor() {

            @Override
            public void visitElement(PsiElement element) {
                if (!isOnTheFly) {
                    if (element.getContainingFile().getVirtualFile().getExtension().toLowerCase().equals("pcf")) {
                        if (element instanceof XmlTag && ((XmlTag) element).getLocalName().toLowerCase().contains("postonchange")) {
                            for (XmlAttribute xmlAttribute : ((XmlTag) element).getAttributes()) {
                                if (xmlAttribute.getLocalName().toLowerCase().contains("target")) {
                                    if (xmlAttribute.getValue() == null || xmlAttribute.getValue().isEmpty()) {
                                        holder.registerProblem(element, "Post on change without a target in " + element.getContainingFile().getVirtualFile().getNameWithoutExtension(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                        return;
                                    } else {
                                        holder.registerProblem(element, "Post on change in " + element.getContainingFile().getVirtualFile().getNameWithoutExtension(), ProblemHighlightType.WEAK_WARNING);
                                    }
                                } else {
                                    holder.registerProblem(element, "Post on change without a target in " + element.getContainingFile().getVirtualFile().getName(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                }
                            }

                        }
                    }
                }
            }
        };
    }
}
