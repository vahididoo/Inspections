package com.aviva.gosu.inspections;

import com.aviva.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import gw.lang.reflect.IType;
import gw.plugin.ij.lang.psi.api.types.IGosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuFieldAccessExpressionImpl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This rule searches and recommends replacement of Collection.where(cond).first() with firstWhere(cond)
 */
public class CollectionWhereTypeIsHasElements extends BaseLocalInspectionTool {

    PsiElement ElsePosition;
    PsiElement leftBracePosition;

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Aviva Inspection Rules";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Use hasMatch(\\e -> e typeis T instead of whereTypeIs(type).HasElements";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "WhereTypeIsHasElements";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new GosuElementVisitor() {

            @Override
            public void visitFieldAccessExpression(GosuFieldAccessExpressionImpl propertyMemberAccessExpression) {

                System.out.println(propertyMemberAccessExpression.getText());

                //first identify the Count, which may not be the last method call
                IGosuCodeReferenceElement lefter = propertyMemberAccessExpression;
                while (lefter != null && !"HasElements".equals(lefter.getReferenceName())) {
                    lefter = lefter.getQualifier();
                }

                //method lookup from right to left, first locate "Count"
                if (lefter!=null && "HasElements".equals(lefter.getReferenceName()) ) {
                    //now locate where(\a -> true). This "where" method is a common method for collection, therefore just need to checker
                    //the method expression  is a colleciton.
                    IGosuCodeReferenceElement firstCaller = lefter.getQualifier();
                    if ("whereTypeIs".equals(firstCaller.getReferenceName()) && isCollection(firstCaller.getQualifier())) {
                        holder.registerProblem(propertyMemberAccessExpression, "Use collections.hasMatch(\\e -> e typeis T)", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        System.out.println("*****Found one instance!*****\n");
                    }
                }
            }
        };
    }

    private boolean isCollection(IGosuCodeReferenceElement expression) {
        IType type = expression.getParsedElement().getReturnType();
        IType[] implInterfaces = type.getInterfaces();
        for (IType implInterface : implInterfaces) {
            int firstLT = implInterface.getName().indexOf("<");
            String infUntypeName = firstLT>0?implInterface.getName().substring(0, firstLT):implInterface.getName();
            //check if this is one of the subclass of Java Collection interfaces that supports "where"
            if (Common.COLLECTIONSUPPORTWHERE.contains(infUntypeName)) {
                return true;
            }
        }

        //check if it is a gosu Array.
        return type.isArray();
    }
}
