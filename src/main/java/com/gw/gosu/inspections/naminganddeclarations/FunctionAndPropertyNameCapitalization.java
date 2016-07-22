
package com.gw.gosu.inspections.naminganddeclarations;

import com.gw.gosu.quickfixes.naminganddeclarations.FunctionAndPropertyNameCapitalizationQuickFix;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Function and Property Name Capitalization Inspection & Quickfix:
 * This plugin is designed to encourage programmers with a cleaner coding style.
 * It suggests to use a lowercase letter at the beginning of function names and uppercase letters for property names
 *
 * The quickfix will help to enforce this convention
 */

public class FunctionAndPropertyNameCapitalization extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("naminganddeclarations.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("naminganddeclarations.fix.tooltip.functionandpropertynamecapitalization");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "FunctionAndPropertyNameCapitalization";
    }

    /**
     * The inspection checks for non-lowercase first letter function names
     * and give out a warning message
     * @param holder
     * @param isOnTheFly
     * @return null
     */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethod(final IGosuMethod method) {
                if (method.getNameIdentifier() == null || method.getNameIdentifier().getText().isEmpty()) {
                    return;
                }

                if (method.isForProperty()) {
                    if (!Character.isUpperCase(method.getName().charAt(0))) {
                        this.registerProblem(method, holder, true);
                    }
                } else {
                    if (!Character.isLowerCase(method.getName().charAt(0))) {
                        this.registerProblem(method, holder, false);
                    }
                }
            }

            private void registerProblem(IGosuMethod method, ProblemsHolder holder, boolean isProperty) {
                final String messageId = isProperty
                        ? "naminganddeclarations.fix.tooltip.propertynameuppercase"
                        : "naminganddeclarations.fix.tooltip.functionnamelowercase";

                holder.registerProblem(
                        method.getNameIdentifier(),
                        SampleBundle.message(messageId),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new FunctionAndPropertyNameCapitalizationQuickFix(method.getNameIdentifier(), method, isProperty));
            }
        };
    }
}

