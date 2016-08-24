package com.gwservices.inspections.pcf;

import com.intellij.codeInspection.*;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 7/12/2016.
 */
public class CheckPostOnCheck extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new PsiElementVisitor() {

            @Override
            public void visitElement(PsiElement element) {
                if (!isOnTheFly) {
                    if (element.getContainingFile().getVirtualFile().getExtension().toLowerCase().equals("pcf")) {
                        if (element instanceof XmlTag && ((XmlTag) element).getLocalName().toLowerCase().contains
                                ("postonchange")) {
                            for (XmlAttribute xmlAttribute : ((XmlTag) element).getAttributes()) {
                                if (xmlAttribute.getLocalName().toLowerCase().contains("target")) {
                                    if (xmlAttribute.getValue() == null || xmlAttribute.getValue().isEmpty()) {
                                        holder.registerProblem(element, "Post on change without a target <code> " +
                                                element.getContainingFile().getVirtualFile().getName() + "</code>",
                                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                        return;
                                    }
                                } else {
                                    holder.registerProblem(element, "Post on change without a target <code> " +
                                            element.getContainingFile().getVirtualFile().getName() + "</code>",
                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                                }
                            }

                        }
                    }
                }
            }
        };
    }
}
