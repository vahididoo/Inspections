package com.gwservices.inspections.pcf;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.lang.annotation.ProblemGroup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/15/2016.
 */
public class PCFComplexVisibleAvailableCheck extends LocalInspectionTool {


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (!isOnTheFly) {
                    if (element.getContainingFile().getVirtualFile().getExtension().toLowerCase().equals("pcf")) {

                        if (element instanceof XmlTag) {
                            String availableProblem = checkTagValue((XmlTag) element, "available");
                            String visibleProblem = checkTagValue((XmlTag) element, "visible");

                            if (visibleProblem != null) {
                                registerProblem(element, "Complex visible condition. <code>" + visibleProblem + "</code>.");
                            } else if (availableProblem != null) {
                                registerProblem(element, "Complex available condition. <code>" + availableProblem + "</code>.");
                            }
                        }
                    }
                }
            }

            private void registerProblem(final PsiElement element, String descriptionTemplate) {
                InspectionManager manager = InspectionManager.getInstance(element.getProject());
                ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(element, descriptionTemplate, (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, false);
                problemDescriptor.setProblemGroup(new ProblemGroup() {
                    @Override
                    public String getProblemName() {
                        return element.getContainingFile().getReference().getCanonicalText();
                    }
                });
                holder.registerProblem(problemDescriptor);
            }

            private String checkTagValue(XmlTag tag, String attrName) {
                XmlAttribute attribute = tag.getAttribute(attrName);
                if (attribute != null) {
                    String value = attribute.getValue();
                    if (value != null && !value.isEmpty() && !value.toLowerCase().equals("true") && !value.toLowerCase().equals("false")) {
                        return value;
                    }
                }
                return null;
            }
        };
    }
}