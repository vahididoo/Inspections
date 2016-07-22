
package com.gw.gosu.inspections.potentialbugs;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.statements.GosuDoWhileStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuForEachStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuWhileStatementImpl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks for a loop that has an empty body.
 */
public class EmptyLoopBody extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("potentialbugs.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("potentialbugs.name.emptyloopbody");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "EmptyLoopBody";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitForEachStatement(final GosuForEachStatementImpl statement) {

                PsiElement body = statement.getLastChild();

                if (body != null && Common.isBlockEmpty(body)) {
                    holder.registerProblem(statement, SampleBundle.message("potentialbugs.name.emptyloopbody"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }

            @Override
            public void visitWhileStatement(final GosuWhileStatementImpl statement) {

                PsiElement body = statement.getLastChild();

                if (body != null && Common.isBlockEmpty(body)) {
                    holder.registerProblem(statement, SampleBundle.message("potentialbugs.name.emptyloopbody"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }

            @Override
            public void visitDoWhileStatement(final GosuDoWhileStatementImpl statement) {

                PsiElement body = statement.getFirstChild()
                                    .getNextSibling(); //Skip over the 'do' keyword

                //Skip over whitespace and comments
                while (body instanceof PsiWhiteSpace || body instanceof GosuCommentImpl) {
                    body = body.getNextSibling();
                }

                if (body != null && Common.isBlockEmpty(body)) {
                    holder.registerProblem(statement, SampleBundle.message("potentialbugs.name.emptyloopbody"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }
        };
    }
}

