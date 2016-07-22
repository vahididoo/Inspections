
package com.gw.gosu.inspections.potentialbugs;

import com.gw.gosu.quickfixes.potentialbugs.EmptyElseQuickFix;
import com.gw.gosu.quickfixes.potentialbugs.EmptyIfWithElseQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks if an if or else block is empty.
 */
public class EmptyIfElse extends BaseLocalInspectionTool {
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
        return SampleBundle.message("potentialbugs.name.else");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "EmptyElse";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitIfStatement(final GosuIfStatementImpl elem) {


                PsiElement current = elem.getFirstChild();
                PsiElement ifBody, elsePart;
                boolean isIfEmpty, hasElsePart, isElseEmpty = true;

                //
                // Firstly, go to the body of the if statement as that is what we need to inspect
                //

                //Skip to the end of the condition statement;
                // the end marker is a closing parenthesis ')'
                while (current != null
                        && !Common.isElementType(current, GosuElementTypes.TT_OP_paren_right)) {
                    current = current.getNextSibling();
                }

                if (current == null) {
                    return;
                }

                current = current.getNextSibling();     //Skip over closing parenthesis ')'
                while (current instanceof PsiWhiteSpaceImpl
                        || current instanceof PsiCommentImpl) { //Skip over whitespace and comments
                    current = current.getNextSibling();
                }

                //Check whether the 'if' block is empty
                isIfEmpty = Common.isBlockEmpty(current);
                ifBody = current;

                if (current == null) {
                    return;
                }
                current = current.getNextSibling(); //Skip over 'if' body

                while (current instanceof PsiWhiteSpaceImpl || current instanceof PsiCommentImpl) { //Skip over whitespace and comments
                    current = current.getNextSibling();
                }

                hasElsePart = (Common.isElementType(current, GosuElementTypes.TT_else));

                if (hasElsePart) {
                    elsePart = current;

                    if (current == null) {
                        return;
                    }
                    current = current.getNextSibling(); //Skip over 'else'

                    while (current instanceof PsiWhiteSpaceImpl || current instanceof PsiCommentImpl) { //Skip over whitespace and comments
                        current = current.getNextSibling();
                    }

                    if (current != null && !(current instanceof GosuTokenImpl)) { //Shouldn't have any tokens after the else part
                        //If it's an "else if", this will be handled by the next visit of the visitor. Don't handle it here!

                        //Check whether the 'else' block is empty
                        isElseEmpty = Common.isBlockEmpty(current);

                        if (isElseEmpty) {
                            holder.registerProblem(current, SampleBundle.message("potentialbugs.name.else"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new EmptyElseQuickFix(elsePart));
                        }
                    }
                }

                if (isIfEmpty) {
                    current = elem;
                    if (current instanceof PsiWhiteSpace) { //Skip over the whitespace
                        current = current.getNextSibling();
                    }

                    if (current != null) {
                        if (hasElsePart && !isElseEmpty) {
                            //There is an else part, so the if can be inverted and the else deleted
                            holder.registerProblem(ifBody, SampleBundle.message("potentialbugs.name.if"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new EmptyIfWithElseQuickFix(elem));
                        } else {
                            holder.registerProblem(ifBody, SampleBundle.message("potentialbugs.name.if"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        }
                    }
                }


                super.visitElement(elem);
            }


        };
    }
}

