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

import java.util.HashSet;
import java.util.Set;

/**
 * This rule searches and recommends replacement of Collection.where(cond).first() with firstWhere(cond)
 */
public class CollectionWhereTypeIsCount extends BaseLocalInspectionTool {

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
        return "Use hasMatch(cond) instead of whereTypeIs(type).Count";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "UseHasMatch";
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
                while (lefter != null && !"Count".equals(lefter.getReferenceName())) {
                    lefter = lefter.getQualifier();
                }

                //method lookup from right to left, first locate "Count"
                if (lefter!=null && "Count".equals(lefter.getReferenceName()) && isInInterestedComparison(lefter)) {
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

    private boolean isInInterestedComparison(PsiElement count) {
        PsiElement op;
        if (count.getNextSibling()!=null) {
            op = nextNonWhiteToken(count.getNextSibling());
        }
        else {
            op = nextNonWhiteToken(count.getParent().getNextSibling());
        }
        PsiElement compareTarget = op!=null?getCompareTargetNumber(op):null;
        if ( op != null && compareTarget != null ) {
            switch (getOperator(op)) {
                case "==":
                case ">":
                case "!=":
                    if ("0".equals(compareTarget.getText().trim()))
                        return true;
                case ">=":
                    if ("1".equals(compareTarget.getText().trim()))
                        return true;
            }
        }
        return false;
    }

    private String getOperator( PsiElement nonWhiteOp ) {
        if ("==".equals(nonWhiteOp.getText()) || "!=".equals(nonWhiteOp.getText()) ||
                ">=".equals(nonWhiteOp.getText()) || "<=".equals(nonWhiteOp.getText()) ||
                "<>".equals(nonWhiteOp.getText())) {
            return nonWhiteOp.getText();
        }
        else if (">".equals(nonWhiteOp.getText()) || "<".equals(nonWhiteOp.getText()) ||
                 "!".equals(nonWhiteOp.getText())) {
            PsiElement next = nonWhiteOp.getNextSibling();
            if ((next!= null) && !(next instanceof PsiWhiteSpace)) {
                switch (next.getText()) {
                    case "=":
                    case ">":
                        return nonWhiteOp.getText() + next.getText();
                }
            }
        }
        return nonWhiteOp.getText();
    }

    private PsiElement getCompareTargetNumber( PsiElement afterOp ) {
        PsiElement next = nextNonWhiteToken(afterOp.getNextSibling());
        if ((next!= null) && !(next instanceof PsiWhiteSpace)) {
            switch (next.getText()) {
                case "=":
                case ">":
                    return getCompareTargetNumber(next);
            }
        }
        return next;
    }

    private PsiElement nextNonWhiteToken( PsiElement afterOp ) {
        while (afterOp != null && (afterOp instanceof PsiWhiteSpace || afterOp.getText().isEmpty())) {
            afterOp = afterOp.getNextSibling();
        }
        return afterOp;
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
