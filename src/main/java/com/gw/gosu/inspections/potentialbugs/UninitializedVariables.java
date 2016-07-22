
package com.gw.gosu.inspections.potentialbugs;

import com.gw.gosu.quickfixes.potentialbugs.UninitializedVariablesQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import gw.internal.gosu.parser.statements.VarStatement;
import gw.lang.parser.IParsedElement;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import com.gw.gosu.inspections.SampleBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Uninitialized Variables Inspection & Quickfix:
 * This plugin is designed to encourage programmers with a cleaner coding style and help debug to find errors.
 * It suggests to give an initial value to new declared variables.
 *
 * The quickfix will help you initialize your variables according the date type, object type
 */
public class UninitializedVariables extends BaseLocalInspectionTool {

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
        return SampleBundle.message("potentialbugs.name.uninitializedvariables");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "UninitializedVariables";
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
                // Local var declarations
                // Member var declarations
                // Var statements
                if ((variable.getNameIdentifier() != null)
                        && Common.isElementType(variable,
                                GosuElementTypes.ELEM_TYPE_LocalVarDeclaration,
                                // What are the other types?
                                GosuElementTypes.FIELD,
                                GosuElementTypes.ELEM_TYPE_VarStatement)
                        && !variable.hasInitializer()
                        // Exclude public member variables; gross, but they could be initialized elsewhere
                        && !variable.hasModifierProperty("public")
                        && !isLoopVariable(variable)
                        && !isReadWritePropertyDecl(variable)
                        && !Common.isCaughtExceptionIdentifier(variable)) {
                    //If the variable data type is a primitive data type, then throw a warning
                    UninitializedVariablesQuickFix quickFix = null;
                    PsiElementFactory elementFactory = JavaPsiFacade.getInstance(variable.getProject()).getElementFactory();
                    PsiClassType collection = elementFactory.createTypeByFQClassName("java.util.Collection");
                    PsiClassType map = elementFactory.createTypeByFQClassName("java.util.Map");

                    if (variable.getType() instanceof PsiPrimitiveType
                            || variable.getType().toString().equals("PsiType:String")
                            || collection.isAssignableFrom(variable.getType())
                            || map.isAssignableFrom(variable.getType())
                            || (variable.getType().getArrayDimensions() > 0)) {
                        quickFix = new UninitializedVariablesQuickFix(variable.getNameIdentifier());
                        //if the variable data type is String, then throw a warning
                    } else {
                        assert variable.getType() instanceof PsiClassType;
                        IParsedElement parsedElement = variable.getParsedElement();
                        if (parsedElement instanceof VarStatement) {
                            boolean thisisanInterface = ((VarStatement) parsedElement).getType().isInterface();
                            //If the data type is an interface and not initialized, then the inspection throws a warning message
                            //But no quickfix in this case
                            if (!thisisanInterface){
                                //If the data type is a class and not initialized, then the inspection throws a warning message with quickfix
                                quickFix = new UninitializedVariablesQuickFix(variable.getNameIdentifier());
                            }
                        }
                    }
                    holder.registerProblem(variable,
                            SampleBundle.message("potentialbugs.message.uninitializedvariables", variable.getName()),
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, quickFix);
                }
            }
        };
    }

    private static boolean isLoopVariable(final IGosuVariable variable) {
        return Common.isElementType(variable.getParent(),
                GosuElementTypes.ELEM_TYPE_ForEachStatement);
    }

    private boolean isReadWritePropertyDecl(final IGosuVariable variable) {
        try {
            if (variable instanceof IGosuField) {
                PsiElement propertyElt = ((IGosuField) variable).getPropertyElement();
                if (propertyElt != null) {
                    // Eat previous whitespace
                    PsiElement previousElement = propertyElt.getPrevSibling();
                    while ((previousElement != null)
                            && Common.isElementType(previousElement,
                                    GosuElementTypes.TT_WHITESPACE)) {
                        previousElement = previousElement.getPrevSibling();
                    }
                    return !Common.isElementType(previousElement, GosuElementTypes.TT_readonly);
                }
            }
        } catch (IllegalStateException e)
        {
            final Logger logger = Logger.getInstance(this.getClass().getName());
            logger.error(e);
        }

        return false;
    }
}