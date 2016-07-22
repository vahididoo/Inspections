
package com.gw.gosu.inspections.layout;

import com.gw.gosu.quickfixes.layout.RedundantSemicolonQuickFix;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RedundantSemicolon extends BaseLocalInspectionTool {
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
        return SampleBundle.message("layout.name.semicolon");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "SemiColonCheck";
    }

    /**
     * The inspection checks for redundant semicolons
     * @param holder
     * @param isOnTheFly
     * @return null
     */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            /**
             * This inspection excludes:
             * 1. the Gosu blocks case such as: var adder = \ x : Number, y : Number -> { print("I added!"); return x + y; }
             * 2. If no statements or curly braces between if and else, a semicolon is find to be there
             */
            public void visitElement(final PsiElement elem){
                if (elem instanceof GosuTokenImpl) {
                    GosuTokenImpl token = (GosuTokenImpl) elem;
                    //Search for semi-colons
                    if (token.getTokenType().equals(GosuElementTypes.TT_OP_semicolon)) {
                        if (elem.getParent().getNextSibling() != null){
                            PsiElement current = elem.getParent().getNextSibling().getParent();
                            if (current.textContains('\n')){
                                if (token.getParent().getText().equals(";")){
                                    //1. the Gosu blocks case such as: var adder = \ x : Number, y : Number -> { print("I added!"); return x + y; }
                                    if (token.getParent().getParent().getFirstChild().getText().equals("{")){
                                        holder.registerProblem(elem, SampleBundle.message("layout.fix.tooltip.semicolon"),
                                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new RedundantSemicolonQuickFix(elem));
                                    }
                                }
                                //2. If no statements or curly braces between if and else, a semicolon is find to be there
                                if (!token.getParent().getText().equals(";")){
                                    holder.registerProblem(elem, SampleBundle.message("layout.fix.tooltip.semicolon"),
                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new RedundantSemicolonQuickFix(elem));
                                }
                            }
                        }
                    }
                }
                super.visitElement(elem);
            }
        };
    }
}
