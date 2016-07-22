
package com.gw.gosu.inspections.layout;

import com.gw.gosu.quickfixes.layout.LeftBraceQuickFix;
import com.gw.gosu.quickfixes.layout.RightBraceQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuClassDefinition;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks if the Gosu brackets convention is adhered to.
 */
public class BracketsLayout extends BaseLocalInspectionTool {
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
        return SampleBundle.message("layout.name.bracketslayout");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "BracketsLayout";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {

            @Override
            public void visitClassDefinition(final IGosuClassDefinition elem) {
                if (elem.getPrevSibling() == null) {
                    return;
                }

                PsiElement leftBrace = elem.getGosuLBrace();

                PsiElement rightBrace = elem.getGosuRBrace();
                PsiElement beforeLeftBrace;

                if (leftBrace != null) {
                    beforeLeftBrace = leftBrace.getPrevSibling();
                }
                else {
                    return;
                }

                boolean needSpaceBeforeLeftBrace = Common.spaceBeforeLeftBrace(elem);

                if (leftBrace.getTextLength() > 0 && rightBrace != null) {
                    //Ensure that an empty element is not flagged
                    highlightProblems(leftBrace, rightBrace, beforeLeftBrace, needSpaceBeforeLeftBrace);
                }
            }

            @Override
            public void visitElement(final PsiElement elem) {
                if (elem instanceof GosuTokenImpl
                        && needsBraces(elem)) {
                    PsiElement leftBrace = elem;

                    while (leftBrace != null
                            && !Common.isElementType(leftBrace.getFirstChild(), GosuElementTypes.TT_OP_brace_left)) { //Look for the left brace
                        leftBrace = leftBrace.getNextSibling();
                    }

                    boolean needSpaceBeforeLeftBrace = Common.spaceBeforeLeftBrace(elem);

                    if (leftBrace != null) {
                        leftBrace = leftBrace.getFirstChild();
                        PsiElement rightBrace = leftBrace.getParent().getLastChild();

                        PsiElement beforeLeftBrace =
                                leftBrace.getParent().getPrevSibling();

                        highlightProblems(leftBrace, rightBrace, beforeLeftBrace, needSpaceBeforeLeftBrace);
                    }
                }
                super.visitElement(elem);
            }

            private void highlightProblems(final PsiElement leftBrace, final PsiElement rightBrace, final PsiElement beforeLeftBrace, final boolean needSpaceBeforeLeftBrace)
            {
                if (violatesConventionLeft(leftBrace, rightBrace, beforeLeftBrace, needSpaceBeforeLeftBrace)) {
                    holder.registerProblem(leftBrace,
                            SampleBundle.message("layout.name.bracketslayout.left" + (needSpaceBeforeLeftBrace ? ".space" : "")),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new LeftBraceQuickFix(leftBrace));
                }
                if (violatesConventionRight(leftBrace, rightBrace)) {
                    holder.registerProblem(rightBrace, SampleBundle.message("layout.name.bracketslayout.right"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new RightBraceQuickFix(rightBrace));
                }
            }

            private boolean violatesConventionLeft(final PsiElement leftBrace, final PsiElement rightBrace, final PsiElement beforeLeftBrace, final boolean needSpaceBeforeLeftBrace) {

                if (!Common.hasNoTrailingTokens(leftBrace)) { //There should be nothing following the opening brace
                    return true;
                }
                if (leftBrace.getNextSibling() == null) {
                    return false;
                }
                if (leftBrace.getNextSibling().equals(rightBrace)) {
                    //This is an exception. If we see "{}" then the programmer probably wants to save screen space
                    return false;
                }
                if (beforeLeftBrace == null) {
                    return false;
                }

                if (beforeLeftBrace.textContains('\n')) {
                   //Should not be a newline before the left brace
                    return true;
                }

                //
                //Need to follow the code style settings rules
                //
                if (needSpaceBeforeLeftBrace) {
                    //There should be whitespace before the left brace
                    if (beforeLeftBrace instanceof PsiWhiteSpace) {
                        //It should be a single whitespace
                        if (!beforeLeftBrace.textMatches(" ")) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                else {
                    //There shouldn't be any space before the left brace
                    if (beforeLeftBrace instanceof PsiWhiteSpace) {
                        return true;
                    }
                }

                return false;
            }

            private boolean violatesConventionRight(final PsiElement leftBrace, final PsiElement rightBrace) {
                if (leftBrace.getNextSibling() == null) {
                    return false;
                }
                if (leftBrace.getNextSibling().equals(rightBrace)) {
                    //This is an exception. If we see "{}" then the programmer probably wants to save screen space
                    return false;
                }


                if (rightBrace != null && rightBrace.getPrevSibling() != null) {
                    if (!(rightBrace.getPrevSibling() instanceof PsiWhiteSpace)
                            || !rightBrace.getPrevSibling().getText().contains("\n")) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    /**
     * Checks for any logical block i.e if. for, while, else, construct, catch, switch, try
     *
     * @param elem The elem to be checked
     * @return Returns true if the elem can use braces
     */
    public static boolean needsBraces(final PsiElement elem) {
        return Common.isElementType(elem, GosuElementTypes.TT_if,
                GosuElementTypes.TT_else,
                GosuElementTypes.TT_for,
                GosuElementTypes.TT_while,
                GosuElementTypes.TT_construct,
                GosuElementTypes.TT_catch,
                GosuElementTypes.TT_switch,
                GosuElementTypes.TT_try,
                GosuElementTypes.TT_function,
                GosuElementTypes.TT_property,
                GosuElementTypes.TT_using,
                GosuElementTypes.TT_do);
    }


}