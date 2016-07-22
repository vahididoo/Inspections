
package com.gw.gosu.inspections.layout;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.layout.WhitespaceAfterTernaryOperatorsQuickFix;
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
 * Checks for whitespaces after ternary operators (‘:’ and ‘?’) based on IntelliJ settings Gosu Code Style Spaces - In-Ternary Operators.
 */
public class WhitespaceAfterTernaryOperators extends BaseLocalInspectionTool {
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
        return SampleBundle.message("layout.fix.tooltip.whitespaceafterternaryoperators");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "WhitespaceAfterTernaryOperators";
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
                        intelliJSettingsCodeStyleGosuSpaces = Common.spaceAfterTernaryOperators(elem);
                        //Check for whitespaces before and after operators
                        checkAfterTernaryOperators(elem);
                    }
                }
            }

            /*Method to check whitespaces after every operator*/
            public void checkAfterTernaryOperators(final PsiElement elem) {
                if (elem.getNextSibling() == null) {
                    return;
                }
                if (!(elem.getNextSibling() instanceof PsiWhiteSpace)) {
                    if (intelliJSettingsCodeStyleGosuSpaces) {
                        holder.registerProblem(elem, SampleBundle.message("layout.fix.tooltip.whitespaceafterternaryoperators"),
                                new WhitespaceAfterTernaryOperatorsQuickFix(elem));
                    }
                }
            }
        };
    }
}