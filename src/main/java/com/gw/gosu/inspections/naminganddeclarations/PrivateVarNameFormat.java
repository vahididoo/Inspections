
package com.gw.gosu.inspections.naminganddeclarations;

import com.gw.gosu.quickfixes.naminganddeclarations.PrivateVarNameFormatQuickFix;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Private Var Name Format Inspection & Quickfix:
 * This plugin is designed to encourage programmers with a cleaner coding style.
 * It suggests to use an underscore letter at the beginning of private (default & non-default) names
 *
 * The quickfix will help you change your first letter function name to an underscore
 */

public class PrivateVarNameFormat extends BaseLocalInspectionTool {
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
        return SampleBundle.message("naminganddeclarations.name.privatevarnameformat");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "PrivateVarNameFormat";
    }

    /**
     * The inspection checks for (with and without keyword - 'private') var name without an underscore at the beginning
     * Give out a warning message if it does not begin with an underscore
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
                if (variable.getNameIdentifier() == null) {
                    return;
                }
                else {
                    //In Gosu, by default, var is private; if the name does not begin with an underscore, then give out a warning
                    if (variable.getNameIdentifier().getParent().getFirstChild().getFirstChild() == null) {
                        //Checks for the first token of the variable's parent body, if it is keyword 'var', then:
                        if (variable.getNameIdentifier().getParent().getFirstChild().getNextSibling() == null) {
                            return;
                        }
                        else if (variable.getNameIdentifier().getParent().getFirstChild().getNextSibling().getText().equals("var")){
                            if (variable.getNameIdentifier().getText().charAt(0) != '_'){
                                holder.registerProblem(variable.getNameIdentifier(), SampleBundle.message("naminganddeclarations.fix.tooltip.privatevarnameformat"),
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new PrivateVarNameFormatQuickFix(variable.getNameIdentifier()));
                            }
                        }
                    }
                    //Checks for the first token of the variable's parent body, if it is keyword 'private', then:
                    else if (variable.getNameIdentifier().getParent().getFirstChild().getFirstChild().getText().equals("private")){
                        if (variable.getNameIdentifier().getText().charAt(0) != '_'){
                            holder.registerProblem(variable.getNameIdentifier(), SampleBundle.message("naminganddeclarations.fix.tooltip.privatevarnameformat"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new PrivateVarNameFormatQuickFix(variable.getNameIdentifier()));
                        }
                    }
                }
            }
        };
    }
}

