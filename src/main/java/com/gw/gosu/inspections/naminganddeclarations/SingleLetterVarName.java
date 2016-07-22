
package com.gw.gosu.inspections.naminganddeclarations;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.naminganddeclarations.SingleLetterVarNameQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.types.IGosuTypeElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Single letter var name inspections checks for single letter var name, and then throw a warning.
 * No quickfix for this inspection
 */
public class SingleLetterVarName extends BaseLocalInspectionTool {
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
        return SampleBundle.message("naminganddeclarations.name.singlelettervarname");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "SingleLetterVarName";
    }

    /**
     * The inspection checks for single letter var name and then throw a warning
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
                    if ((variable.getNameIdentifier().getText().length() == 1)
                            && !isLoopVar(variable)
                            && !isExpectedCaughtExceptionIdentifier(variable)) {
                        holder.registerProblem(variable.getNameIdentifier(),
                                SampleBundle.message("naminganddeclarations.name.singlelettervarname"),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                new SingleLetterVarNameQuickFix(variable));
                    }
                }
            }
        };
    }

    /**
     * Common loop variable names.
     * TODO: Make this configurable.
     */
    static final String[] LOOP_VAR_NAMES = new String[] { "i", "j", "x", "y", "z" };

    /**
     * We'll allow single-letter variable names for common loop index
     * variable names (i, j, x, y, z).
     *
     * @param variable
     * @return
     */
    private static boolean isLoopVar(final IGosuVariable variable) {
        boolean found = false;
        String varName = variable.getNameIdentifier().getText();
        for (int i = 0; i < LOOP_VAR_NAMES.length; i++) {
            if (LOOP_VAR_NAMES[i].equals(varName)) {
                found = true;
                break;
            }
        }

        if (found) {
            PsiElement context = variable.getParent();
            return Common.isElementType(context, GosuElementTypes.TT_foreach, GosuElementTypes.ELEM_TYPE_ForEachStatement);
        }

        return false;
    }

    /**
     * We'll also allow a single-letter var name for a caught exception -
     * e if it's an exception type, t if it's a Throwable.
     *
     * @param variable A single-letter-named variable
     * @return True if the variable is a caught exception type named e or a caught Throwable named t
     */
    private static boolean isExpectedCaughtExceptionIdentifier(final IGosuVariable variable) {
        if (Common.isCaughtExceptionIdentifier(variable)) {
            IGosuTypeElement declaredType = variable.getTypeElementGosu();
            if (declaredType == null || declaredType.getType() == null) {
                // Only "e" is valid with no declared type
                return variable.getNameIdentifier().getText().equals("e");
            } else if (declaredType.getType() == null) {
                // This is an edge case when we are in the process of typing the type and only the ':' has been typed.
                // In such case no further inspection makes sense so just return true.
                return true;
            } else {
                // If it's declared as a Throwable, it can be "t"
                if (declaredType.getType().equalsToText("java.lang.Throwable")) {
                    return variable.getNameIdentifier().getText().equals("t");
                }

                // Otherwise, only "e" is allowed
                return variable.getNameIdentifier().getText().equals("e");
            }
        }
        return false;
    }
}
