
package com.gw.gosu.inspections.potentialbugs;

import com.gw.gosu.quickfixes.potentialbugs.UnusedLoopVariableQuickFix;
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
import gw.plugin.ij.lang.psi.impl.expressions.GosuIdentifierImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuForEachStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuVariableImpl;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;


/**
 * Checks if the variable used in a foreach loop is unused.
 */
public class UnusedLoopVariable extends BaseLocalInspectionTool {
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
        return SampleBundle.message("potentialbugs.name.unusedloopvariable");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "UnusedLoopVariable";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitForEachStatement(final GosuForEachStatementImpl statement) {

                PsiElement body = Common.getConditionalBody(statement);

                if (body == null) {
                    return;
                }

                PsiElement current = statement.getFirstChild();

                //Skip over everything until we get to a variable
                while (current != null && !(current instanceof GosuVariableImpl)) {
                    current = current.getNextSibling();
                }

                if (current != null) { //We found a loop variable
                    PsiElement variable = current;

                    //If the variable has no name then an exception will be thrown
                    if (variable.getText().isEmpty()) {
                        return;
                    }

                    while (current != null && !Common.isElementType(current, GosuElementTypes.TT_in)) {
                        current = current.getNextSibling();
                    }

                    boolean isIntervalLoop = false;

                    if (current == null) { //If we are looking at a loop that doesn't use the 'in' keyword then this is the 'index' variable
                        return;
                    }

                    //Otherwise this is a normal loop
                    current = current.getNextSibling(); //Skip over the 'in' keyword

                    while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) { //Skip over whitespace and comments
                        current = current.getNextSibling();
                    }

                    if (Common.isElementType(current, GosuElementTypes.ELEM_TYPE_IntervalExpression)) {
                        isIntervalLoop = true;
                    }


                    boolean varOccurs;

                    if (Common.isElementType(body, GosuElementTypes.ELEM_TYPE_NoOpStatement)) {
                        varOccurs = false;
                    } else {
                        varOccurs = variableOccursIn(variable.getText(), body.getParent());
                    }

                    if (!varOccurs) {
                        if (isIntervalLoop) {
                            holder.registerProblem(variable, SampleBundle.message("potentialbugs.name.unusedloopvariable"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new UnusedLoopVariableQuickFix(statement));
                        } else {
                            holder.registerProblem(variable, SampleBundle.message("potentialbugs.name.unusedloopvariable"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        }
                    }
                }
            }
        };
    }

    /**
     * Checks if a variable name occurs in an element tree
     * @param variableName The name of the variable
     * @param elem The element, which may contain the variable
     * @return True if the variable is found in the element's tree
     */
    private static boolean variableOccursIn(final String variableName, final PsiElement elem) {
        //A type could have the same name as a variable so rule that out here
        if (elem instanceof GosuTypeLiteralImpl)
            return false;

        PsiElement child = elem.getFirstChild();

        if (child == null) { //We are at a leaf node, if there are no children
            if (elem instanceof GosuIdentifierImpl
                    && elem.textMatches(variableName)) {
                return true;
            }
        } else  //Otherwise recurse over the children elements
        {
            while (child != null) {
                if (variableOccursIn(variableName, child)) {
                    return true;
                }
                child = child.getNextSibling();
            }
        }
        return false;
    }

}

