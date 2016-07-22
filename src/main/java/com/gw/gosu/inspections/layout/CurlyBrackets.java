package com.gw.gosu.inspections.layout;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.layout.CurlyBracketsQuickFix;
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
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks if curly brackets are not being used to indicate nesting.
 */
public class CurlyBrackets extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("layout.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("layout.name.curlybrackets");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "CurlyBrackets";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {

            @Override
            public void visitElement(final PsiElement elem) {
                PsiElement current = null;

                if (Common.isElementType(elem,
                        GosuElementTypes.TT_if,
                        GosuElementTypes.TT_while,
                        GosuElementTypes.TT_for)) {
                    current = skipToTheEndOfTheConditionBody(elem);
                } else if (Common.isElementType(elem,
                        GosuElementTypes.TT_else,
                        GosuElementTypes.TT_do)) {
                    //Skip over the else or do
                    current = elem.getNextSibling();
                }

                current = skipOverWhitespaceAndComments(current);

                if (isNotOpeningCurlyBracket(current) && isNotElseIfStatement(current)) {
                    // Highlight the whole statement, not just the keyword
                    holder.registerProblem(elem.getParent(), SampleBundle.message("layout.name.curlybrackets"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CurlyBracketsQuickFix(current));
                }

                super.visitElement(elem);
            }
        };
    }

    private boolean isNotOpeningCurlyBracket(PsiElement current) {
        return current != null && !Common.isElementType(current.getFirstChild(), GosuElementTypes.TT_OP_brace_left);
    }

    private boolean isNotElseIfStatement(PsiElement current) {
        return current != null && !Common.isElementType(current, GosuElementTypes.ELEM_TYPE_IfStatement);
    }

    private PsiElement skipOverWhitespaceAndComments(PsiElement current) {
        //Skip over whitespace and comments
        while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) {
            current = current.getNextSibling();
        }
        return current;
    }

    private PsiElement skipToTheEndOfTheConditionBody(PsiElement current) {
        while (current != null && !Common.isElementType(current.getPrevSibling(), GosuElementTypes.TT_OP_paren_right)) {
            current = current.getNextSibling();
        }
        return current;
    }
}