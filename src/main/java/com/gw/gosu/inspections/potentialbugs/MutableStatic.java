package com.gw.gosu.inspections.potentialbugs;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Static members that are collections but not Weak could indicate a memory
 * leak.
 */
public class MutableStatic extends BaseLocalInspectionTool {
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
        return SampleBundle.message("potentialbugs.name.mutablestatic");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "MutableStatic";
    }

    /*
    * Inspection: Checks for uninitialized variables
    * */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder,
                                                final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            public void visitVariable(final IGosuVariable variable) {
                // Member var declarations
                if (Common.isElementType(variable, GosuElementTypes.FIELD)
                        && variable.hasModifierProperty("static")) {
                    PsiElementFactory elementFactory = JavaPsiFacade.getInstance(variable.getProject()).getElementFactory();
                    PsiClassType collection = elementFactory.createTypeByFQClassName("java.util.Collection");
                    PsiClassType map = elementFactory.createTypeByFQClassName("java.util.Map");
                    PsiClassType weakHashMap = elementFactory.createTypeByFQClassName("java.util.WeakHashMap");

                    // Add a warning if the static var is a collection type
                    if (collection.isAssignableFrom(variable.getType())
                            || (map.isAssignableFrom(variable.getType())
                                && !weakHashMap.isAssignableFrom(variable.getType()))) {
                        holder.registerProblem(variable,
                                SampleBundle.message("potentialbugs.message.mutablestatic", variable.getName()),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                    }
                }
            }
        };
    }
}
