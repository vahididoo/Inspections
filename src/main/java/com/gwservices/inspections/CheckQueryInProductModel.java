package com.gwservices.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/16/2016.
 */
public class CheckQueryInProductModel extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        super.buildVisitor(holder, isOnTheFly);
        return new XmlElementVisitor() {

            @Override
            public void visitXmlTag(XmlTag tag) {
                super.visitXmlTag(tag);

                if (!tag.getContainingFile().getVirtualFile().getCanonicalPath().contains("/productmodel/")) {
                    return;
                }

                if ("AvailabilityScript".equalsIgnoreCase(tag.getName())) {
                    if (tag.getText() != null && tag.getText().contains("Query.make")) {
                        holder.registerProblem(tag.getParent() != null ? tag.getParent() : tag, "Using database "
                                + "operations in product model availability.", ProblemHighlightType.WEAK_WARNING);
                    }
                }
                return;
            }
        };
    }
}
