
package com.gw.gosu.inspections.comments;

import com.gw.gosu.inspections.SampleBundle;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.javadoc.PsiDocComment;
import com.gw.gosu.quickfixes.comments.GosudocCommentsQuickFix;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Gosudoc Comments Inspection & Quickfix:
 * This plugin is designed to check for doc comments before method definitions which is meant to be a good coding habit
 * and makes other programmers easier to understand and read your code.
 *
 * The quickfix will help you add a Gosu code style doc comment just right above your method definition.
 */

public class GosudocComments extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("comments.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("comments.fix.tooltip.gosudoccomments");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "GosudocComments";
    }

    /**
     * Check for doc comments before function definitions
     * If no doc comments, then display a warning message
     * @param holder
     * @param isOnTheFly
     * @return
     */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethod(final IGosuMethod method) {
                //If there is no method definitions, then skip
                if (method.getParsedElement() == null) {
                    return;
                }

                //If there is a method but with no method name such as constructors, then skip
                if (method.getNameIdentifier() == null || method.getNameIdentifier().getText().isEmpty()){
                    return;
                }

                // comments only required for public functions
                if (!methodIsPublic(method)) {
                    return;
                }

                if (methodHasDocComment(method)) {
                    return;
                }

                holder.registerProblem(
                        method.getNameIdentifier(),
                        SampleBundle.message("comments.fix.tooltip.gosudoccomments"),
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        new GosudocCommentsQuickFix(method.getNameIdentifier()));
            }

            private boolean methodIsPublic(IGosuMethod method) {
                return !method.getModifierList().hasModifierProperty(GosuElementTypes.TT_public.toString());
            }

            private boolean methodHasDocComment(IGosuMethod method) {
                PsiElement previousElement = method.getPrevSibling();
                while (previousElement != null && previousElement instanceof PsiWhiteSpace) {
                    previousElement = previousElement.getPrevSibling();
                }

                return previousElement instanceof PsiDocComment;
            }
        };
    }
}
