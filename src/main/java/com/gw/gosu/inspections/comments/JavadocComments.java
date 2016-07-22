
package com.gw.gosu.inspections.comments;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.comments.JavadocCommentsQuickFix;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.javadoc.PsiDocComment;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Javadoc Comments Inspection & Quickfix:
 * This plugin is designed to check for doc comments before method definitions which is meant to be a good coding habit
 * and makes other programmers easier to understand and read your code.
 *
 * The quickfix will help you add a Java code style doc comment just right above your method definition.
 */

public class JavadocComments extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("comments.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("comments.fix.tooltip.javadoccomments");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "JavadocComments";
    }

    /**
     * Check for doc comments before function definitions
     * If no doc comments, then display a warning message
     * @param holder
     * @param isOnTheFly
     * @return null
     */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitMethod(final IGosuMethod method) {
                IParsedElement parsedElement = method.getParsedElement();
                //If there is no method definitions, then just return null
                if (parsedElement == null) {
                    return;
                }
                //If there is a method but with no method name such as constructors, then just return null
                if (method.getNameIdentifier() == null){
                    return;
                } else {
                    //If there is a method name, then:
                    if (!method.getNameIdentifier().getText().isEmpty()) {
                        //If one line above the method definition, there is doc comments, then just return null
                        if (method.getPrevSibling().getPrevSibling() instanceof PsiDocComment) {
                            return;
                        } else {
                            //If there is no doc comment, then give a warning message back to the programmer
                            //And suggesting to add doc comments
                            holder.registerProblem(method.getNameIdentifier(), SampleBundle.message("comments.fix.tooltip.javadoccomments"),
                                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new JavadocCommentsQuickFix(method.getNameIdentifier()));
                        }
                    }
                }
            }
        };
    }
}