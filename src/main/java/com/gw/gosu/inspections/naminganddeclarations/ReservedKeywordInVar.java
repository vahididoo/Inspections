
package com.gw.gosu.inspections.naminganddeclarations;

import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Reserved Keyword 'native' var name inspection & quickfix
 * This is an inspection to check the reserved keywords 'native' is not used as a var name
 *
 * No quickfix for this inspection.
 */

public class ReservedKeywordInVar extends BaseLocalInspectionTool {
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
        return SampleBundle.message("naminganddeclarations.name.reservedkeywordinvar");
    }

    @Override
    public final  boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "ReservedKeywordInVar";
    }

    /**
     * Checks for a Java style line termination (i.e. a semicolon) and
     * suggests this be deleted in accordance with the Gosu coding style.
     *
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
                if (variable.getNameIdentifier() != null) {
                    if (variable.getNameIdentifier().getText().equals("native")) {
                        holder.registerProblem(variable.getNameIdentifier(), SampleBundle.message("naminganddeclarations.name.reservedkeywordinvar"),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
                }
            }
        };
    }
}




