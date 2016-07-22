
package com.gw.gosu.inspections.naminganddeclarations;

import com.gw.gosu.quickfixes.naminganddeclarations.InferExceptionTypeQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks if the type 'Exception' is explicitly declared, because it is implied anyway
 */
public class InferExceptionType extends BaseLocalInspectionTool {
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
        return SampleBundle.message("naminganddeclarations.name.exceptiontype");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "InferExceptionType";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitElement(final PsiElement elem) {

                if (Common.isElementType(elem, GosuElementTypes.TT_catch)) {
                    PsiElement current = elem.getNextSibling(); //Skip over the 'catch'

                    while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) { //Skip over whitespace and comments
                        current = current.getNextSibling();
                    }

                    current = current.getNextSibling(); //Skip over the '('

                    while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) { //Skip over whitespace and comments
                        current = current.getNextSibling();
                    }

                    if (current.getLastChild() instanceof GosuTypeLiteralImpl) {
                        if (current.getLastChild().textMatches("Exception")) {
                            holder.registerProblem(current, SampleBundle.message("naminganddeclarations.name.exceptiontype"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new InferExceptionTypeQuickFix(current));
                        }
                    }
                }
            }
        };
    }
}