
package com.gw.gosu.inspections.layout;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.layout.WhitespaceAroundOperatorsQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import gw.internal.gosu.parser.SourceCodeTokenizerInternal;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks for whitespaces around operators based on IntelliJ settings Gosu Code Style Spaces - Around Operators.
 */
public class WhitespaceAroundOperators extends BaseLocalInspectionTool {
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
        return SampleBundle.message("layout.fix.tooltip.whitespacearoundoperators");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "WhiteSpaceAroundOperators";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            private boolean intelliJSettingsCodeStyleGosuSpaces;
            //creates a new String set of default operators - code from Luca Boasso
            @Override
            public void visitElement(final PsiElement elem) {
                if (elem instanceof GosuTokenImpl) {
                    GosuTokenImpl t = (GosuTokenImpl) elem;
                    if (t.textContains('.')) {
                        return;
                    }
                    final Set<String> DEFAULT_OPERATORS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(SourceCodeTokenizerInternal.getDefaultOperators())));
                    //Detect if it is an operator
                    boolean isOperator = DEFAULT_OPERATORS.contains(t.getText());
                    if (isOperator) {
                        //Get IntelliJ settings Gosu code style
                        intelliJSettingsCodeStyleGosuSpaces = Common.spaceAroundOperators(elem);
                        //Check for whitespaces before and after operators
                        checkSpaceBeforeAndAfterOperator(elem);
                    }
                }
            }

            //function to check if a whitespace occurs before and after it
            public void checkSpaceBeforeAndAfterOperator(final PsiElement elem) {
                if (checkBefore(elem) || checkAfter(elem)) {
                    if (intelliJSettingsCodeStyleGosuSpaces) {
                        holder.registerProblem(elem, SampleBundle.message("layout.fix.tooltip.whitespacearoundoperators"),
                                new WhitespaceAroundOperatorsQuickFix(elem));
                    }
                }
            }

            /*Method to check the spacing before an operator*/
            public boolean checkBefore(final PsiElement elem) {
                // if there is no previous sibling reference, we need to investigate parents of the current element.
                if (elem.getPrevSibling() == null) {
                    // if the parent elements previous sibling is a left bracket - no error so return
                    if (elem.getParent().getParent().getPrevSibling() instanceof GosuTokenImpl) {
                        return false;
                    }
                    // if the parents parents previous sibling is a whitespace - no error, return
                    // the reason for this case is when I had     'some method call ( - i,  variable)'
                    // this exposed errors
                    if (elem.getParent().getParent().getPrevSibling() instanceof PsiWhiteSpace) {
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