package com.gwservices.inspections.pcf;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import org.apache.commons.lang.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 7/18/2016.
 */
public class CheckHardcodedLabel extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if ("pcf".equalsIgnoreCase(element.getContainingFile().getVirtualFile().getExtension())) {
                    if (element instanceof XmlAttribute) {
                        if ("label".equalsIgnoreCase(((XmlAttribute) element).getLocalName())) {
                            String value = ((XmlAttribute) element).getValue();
                            if (value != null && value.trim().startsWith("&quot;")) {
                                String unescapeHtml = StringEscapeUtils.unescapeHtml(StringUtils.substringBetween
                                        (value, "&quot;"));
                                if (unescapeHtml != null && !StringUtil.isEmptyOrSpaces(unescapeHtml)) {
                                    holder.registerProblem(element.getParent(), "Hardcoded lables <code>" + (
                                            (XmlAttribute) element).getName() + "</code>");
                                }
                            }
                        }
                    }
                }
            }
        };
    }
}
