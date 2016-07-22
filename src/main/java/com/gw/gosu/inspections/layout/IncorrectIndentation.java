
package com.gw.gosu.inspections.layout;

import com.gw.gosu.quickfixes.layout.IndentationQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import gw.lang.parser.statements.INoOpStatement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.statements.GosuForEachStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuWhileStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuExtendsClauseImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuImplementsClauseImpl;
import gw.plugin.ij.lang.psi.impl.types.GosuTypeVariableListImpl;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Checks if the indentation at the start of each line is aligned to the intellij project settings indentation.
 */
public class IncorrectIndentation extends BaseLocalInspectionTool {
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
        return SampleBundle.message("layout.name.indent");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "Indentation";
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitIfStatement(final GosuIfStatementImpl elem) //Must visit if statements, because these are not visited by visitElement
            {
                visitStatement(elem.getFirstChild(), Common.getIfBody(elem), false);
            }

            @Override
            public void visitWhileStatement(final GosuWhileStatementImpl elem) //Must visit while loops, because these are not visited by visitElement
            {
                visitStatement(elem.getFirstChild(), Common.getConditionalBody(elem), false);
            }

            @Override
            public void visitMethod(final IGosuMethod elem) {
                PsiElement body = Common.getConditionalBody(elem);
                if (body != null) {
                    visitStatement(elem.getFirstChild(), body, isRulesFile(elem));
                }
            }

            @Override
            public void visitForEachStatement(final GosuForEachStatementImpl elem) {
                visitStatement(elem.getFirstChild(), Common.getConditionalBody(elem), false);
            }

            @Override
            public void visitClassDefinition(final IGosuClassDefinition elem) {
                PsiElement bodyStarts = elem.getFirstChild();

                //Keep going until the class name is reached(The body follows that)
                while (!(bodyStarts instanceof PsiIdentifier)) {
                    bodyStarts = bodyStarts.getNextSibling();
                }

                bodyStarts = bodyStarts.getNextSibling(); //Skip over the class name

                while (bodyStarts instanceof PsiWhiteSpace || bodyStarts instanceof PsiCommentImpl  //Skip over whitespace and comments
                        || bodyStarts instanceof GosuExtendsClauseImpl // Skip over extends clause
                        || bodyStarts instanceof GosuImplementsClauseImpl //Skip over implements clause
                        || bodyStarts instanceof GosuTypeVariableListImpl) { //Skip over the Generics clause
                    bodyStarts = bodyStarts.getNextSibling();
                }

                if (!Common.isElementType(bodyStarts, GosuElementTypes.TT_OP_brace_left)) {
                    //This is a special case where we are at the base class of the file
                    //This class is created by gosu because java requires a class implementation per class file
                    //So the indentation should be 0
                    checkAndHighlightIndent(bodyStarts, 0);
                } else {
                    visitStatement(elem.getFirstChild(), bodyStarts, false);
                }
            }

            private void visitStatement(final PsiElement firstElem, final PsiElement bodyElem, final boolean resetIndent) {
                PsiElement current = bodyElem;

                //
                //Get the expected indent within this block
                //
                if (Common.isElementType(current, GosuElementTypes.TT_OP_brace_left)) {
                    current = Common.advanceToNextLineOuter(current); //Skip over the left brace
                }

                //If the next element is on the same line, then don't check indentation
                if (!isStatementOnTheSameLine(current)) {
                    //Add the default indent, because everything following a curly brace is indented by an extra tab
                    int indent = resetIndent ? 0 : Common.getStartOfLineIndent(firstElem) + indentSize(firstElem);

                    checkAndHighlightIndent(current, indent);
                }
            }

            private void checkAndHighlightIndent(final PsiElement elem, final int originalIndentation) {
                PsiElement current = elem;

                do {
                    int expectedIndentation = originalIndentation;

                    if (Common.isElementType(current, GosuElementTypes.TT_OP_brace_right, GosuElementTypes.TT_else)) {
                        expectedIndentation -= indentSize(current);
                    }

                    int indent = Common.getStartOfLineIndent(current);

                    if (indent != expectedIndentation) {

                        //Ignore empty statements
                        if (current == null || current.getText().isEmpty() || current instanceof INoOpStatement) {
                            return;
                        }

                        holder.registerProblem(
                                current,
                                SampleBundle.message("layout.name.indent"),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                new IndentationQuickFix(current));
                    }

                    //At the end of a block (e.g. an if statement) the last child is null
                    if (current == null || current.getNextSibling() == null
                            || Common.isElementType(current, GosuElementTypes.TT_else)) { //Ignore everything after the else
                        return;
                    }

                    current = Common.advanceToNextLineOuter(current); //Skip to the beginning of the next line
                }
                while (current != null);
            }
        };
    }

    private boolean isRulesFile(PsiElement elem) {
        return elem.getContainingFile().getName().endsWith(".gr");
    }

    private int indentSize(PsiElement firstElem) {
        return Common.getIndentSize(firstElem.getProject(), firstElem.getContainingFile().getFileType());
    }

    private boolean isStatementOnTheSameLine(PsiElement current) {
        return current == null //If the body is null, then multiple statements are probably on the same line
                || (current.getPrevSibling() instanceof PsiWhiteSpace && !current.getPrevSibling().textContains('\n'));
    }
}
