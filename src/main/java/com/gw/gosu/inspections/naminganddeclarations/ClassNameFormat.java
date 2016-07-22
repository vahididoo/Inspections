
package com.gw.gosu.inspections.naminganddeclarations;

import com.gw.gosu.quickfixes.naminganddeclarations.ClassNameFormatQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuClassDefinition;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import com.gw.gosu.utility.NamingConventionsHelper;

/**
 * Check if class names start with a capital letter, as they should.
 */
public class ClassNameFormat extends BaseLocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return SampleBundle.message("naminganddeclarations.group.name");
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return SampleBundle.message("naminganddeclarations.name.classNames");
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public String getShortName() {
        return "ClassNameCheck";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitClassDefinition(final IGosuClassDefinition classDefinition) {
                IParsedElement parsedElement = classDefinition.getParsedElement();
                if (parsedElement == null) {
                    return;
                }
                //Exclude the file class (.gs/.gsp name)
                if (!classDefinition.getNameIdentifier().getText().isEmpty()
                        && !NamingConventionsHelper.isWellFormattedClass(classDefinition.getNameIdentifier().getText())) {
                    holder.registerProblem(classDefinition.getNameIdentifier(), SampleBundle.message("naminganddeclarations.name.classNames"),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new ClassNameFormatQuickFix(classDefinition.getNameIdentifier()));
                }
            }
        };
    }
}