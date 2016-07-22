package com.aviva.gosu.inspections;

import com.gw.gosu.common.InspectionsTestBase;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.expressions.GosuFieldAccessExpressionImpl;
import org.mockito.Mockito;

import java.util.List;

/**
 * Created by tp_1996397 on 6/25/2015.
 */
public class CollectionWhereHasElementsTest extends InspectionsTestBase {
    public void testCollectionWhereFirst() {
//        final IGosuStatementList startElement = Common.searchForElementOfType(this.gosuSourceCode, GosuStatementListImpl.class);
//
        ProblemsHolder problemsHolderMock = Mockito.mock(ProblemsHolder.class);
//        ((GosuElementVisitor)new CollectionWhereFirst().buildVisitor(problemsHolderMock, false)).visitStatementList(startElement);

//        final GosuBeanMethodCallExpressionImpl beanMethod1 = Common.searchForElementOfType(this.gosuSourceCode, GosuBeanMethodCallExpressionImpl.class);
//        ((GosuElementVisitor)new CollectionWhereFirst().buildVisitor(problemsHolderMock, false)).visitBeanMethodCallExpression(beanMethod1);


        final List<PsiElement> beanMethods = Common.searchForElementsOfType(this.gosuSourceCode, GosuFieldAccessExpressionImpl.class);
        for (PsiElement beanMethod : beanMethods) {
            if ( beanMethod instanceof GosuFieldAccessExpressionImpl ) {
                ((GosuElementVisitor)new CollectionWhereHasElements().buildVisitor(problemsHolderMock, false)).visitFieldAccessExpression((GosuFieldAccessExpressionImpl) beanMethod);
            }
        }

//        System.out.println(problemsHolderMock.hasResults());
//        System.out.println(problemsHolderMock.getResults().size());
    }
}
