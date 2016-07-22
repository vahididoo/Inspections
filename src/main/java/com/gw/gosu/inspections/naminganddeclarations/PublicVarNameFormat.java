
package com.gw.gosu.inspections.naminganddeclarations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.naminganddeclarations.PublicVarNameFormatQuickFix;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Public Var Name Format Inspection & Quickfix:
 * This plugin is designed to encourage programmers with a cleaner coding style.
 * It suggests to use an uppercase letter at the beginning of private (default & non-default) names
 *
 * The quickfix will help you change your first letter function name to an uppercase
 */

public class PublicVarNameFormat extends BaseLocalInspectionTool {
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
        return SampleBundle.message("naminganddeclarations.name.publicvarnameformat");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "PublicVarNameFormat";
    }

    /**
     * The inspection checks for public var name if it begins with an uppercase letter or not
     * If not, a warning will be thrown.
     * @param holder
     * @param isOnTheFly
     * @return null
     */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitVariable(final IGosuVariable variable) {
                //Avoid null pointer
                if (variable.getNameIdentifier() != null
                        && variable.getNameIdentifier().getParent() != null
                        && variable.getNameIdentifier().getParent().getFirstChild() != null
                        && variable.getNameIdentifier().getParent().getFirstChild().getFirstChild() != null) {
                    int warningIt = 0;
                    //If the name does not begin with an uppercase, then give out a warning
                    if (variable.getNameIdentifier().getParent().getFirstChild().getFirstChild().getText().equals("public")) {
                        for (char i = 'A'; i <= 'Z'; i++) {
                            if (variable.getNameIdentifier().getText().charAt(0) == i) {
                                warningIt++;
                            }
                        }
                        if (warningIt == 0) {
                            holder.registerProblem(variable.getNameIdentifier(), SampleBundle.message("naminganddeclarations.fix.tooltip.publicvarnameformat"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new PublicVarNameFormatQuickFix(variable.getNameIdentifier()));
                        }
                    }
                }
            }
        };
    }
}

