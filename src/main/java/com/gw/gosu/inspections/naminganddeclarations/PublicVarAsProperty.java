
package com.gw.gosu.inspections.naminganddeclarations;

import com.gw.gosu.quickfixes.naminganddeclarations.PublicVarAsPropertyQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.internal.gosu.parser.statements.VarStatement;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;


/**
 * Checks if a public variable is backed by a private variable
 * (i.e. it is a property, not a field)
 */
public class PublicVarAsProperty extends BaseLocalInspectionTool {

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
        return SampleBundle.message("naminganddeclarations.name.publicvarasproperty");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "PublicVarAsProperty";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitVariable(final IGosuVariable field) {
                IParsedElement parsedElement = field.getParsedElement();
                if (parsedElement == null) {
                    return;
                }
                if (parsedElement instanceof VarStatement) {
                    VarStatement varStmt = (VarStatement) parsedElement;
                    if (varStmt.isPublic()) {
                        holder.registerProblem(field, SampleBundle.message("naminganddeclarations.name.publicvarasproperty"),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new PublicVarAsPropertyQuickFix(field));
                    }
                }
            }
        };
    }
}
