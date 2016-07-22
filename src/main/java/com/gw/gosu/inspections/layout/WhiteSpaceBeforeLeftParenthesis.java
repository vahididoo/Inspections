
package com.gw.gosu.inspections.layout;

import com.gw.gosu.quickfixes.layout.WhiteSpaceBeforeLeftParenthesisQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks for whitespaces before left parenthesis based on IntelliJ settings Gosu Code Style Spaces.
 */
public class WhiteSpaceBeforeLeftParenthesis extends BaseLocalInspectionTool {
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
        return SampleBundle.message("layout.fix.tooltip.whitespacebeforeleftparenthesis");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "WhiteSpaceBeforeLeftParenthesis";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            private boolean intelliJSettingsCodeStyleGosuSpaces;

            @Override
            public void visitElement(final PsiElement elem) {
                if (elem instanceof GosuTokenImpl) {
                    GosuTokenImpl token = (GosuTokenImpl) elem;
                    //If the token is a '(' or ')', then:
                    if (token.getText().equals("(") || token.getText().equals(")")) {
                        //Check for brackets pairs such as "print()", no warning message displayed here
                        for (int counter = 0; counter < token.getParent().getTextLength(); counter++) {
                            if (token.getParent().getText().charAt(counter) == '(') {
                                if (token.getParent().getText().charAt(counter + 1) == ')') {
                                    return;
                                }
                            }
                        }

                        //If it is not bracket pairs, then:
                        intelliJSettingsCodeStyleGosuSpaces = Common.spaceBeforeParentheses(elem);
                        checkSpaceBeforeAndAfterOperator(elem);
                    }
                }
                super.visitElement(elem);
            }

            public void checkSpaceBeforeAndAfterOperator(final PsiElement elem) {
                //either there is no whitespace before or after right or left bracket, a warning message will be displayed here:
                if (elem.getText().equals("(") && checkAfter(elem)) {
                    if (intelliJSettingsCodeStyleGosuSpaces && checkBefore(elem)) {
                        holder.registerProblem(elem, SampleBundle.message("layout.fix.tooltip.whitespacebeforeleftparenthesis"), new WhiteSpaceBeforeLeftParenthesisQuickFix(elem));
                    }
                    else if (!intelliJSettingsCodeStyleGosuSpaces && !checkBefore(elem)) {
                        holder.registerProblem(elem, SampleBundle.message("layout.fix.tooltip.whitespacebeforeleftparenthesis"), new WhiteSpaceBeforeLeftParenthesisQuickFix(elem));
                    }
                }
            }

            /*Method to check the spacing before an operator*/
            public boolean checkBefore(final PsiElement elem) {
                // if there is no previous sibling reference, we need to investigate parents of the current element.
                if (elem.getPrevSibling() == null) {
                    // if the parent elements previous sibling is a left bracket - no error so return
                    if (elem.getParent().getPrevSibling() instanceof GosuTokenImpl) {
                        return false;
                    }

                    // if the parents parents previous sibling is a whitespace - no error, return
                    // the reason for this case is when I had     'some method call ( - i,  variable)'
                    // this exposed errors
                    if (elem.getParent().getPrevSibling() instanceof PsiWhiteSpace) {
                        return false;
                    }
                }

                if (!(elem.getPrevSibling() instanceof PsiWhiteSpace) && (elem.getPrevSibling() != null)) {
                    return true;
                }
                
                return false;
            }

            /*Method to check after every operator*/
            public boolean checkAfter(final PsiElement elem) {
                if (elem.getNextSibling() == null) {
                    return false;
                }

                if (!(elem.getNextSibling() instanceof PsiWhiteSpace)) {
                    return true;
                }

                return false;
            }
        };
    }
}


