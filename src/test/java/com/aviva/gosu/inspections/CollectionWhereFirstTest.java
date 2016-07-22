package com.aviva.gosu.inspections;

import com.gw.gosu.common.InspectionsTestBase;
import com.gw.gosu.inspections.layout.CurlyBrackets;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import gw.internal.gosu.parser.statements.VarStatement;
import gw.plugin.ij.debugger.evaluation.GosuExpressionEvaluatorImpl;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.GosuElementType;
import gw.plugin.ij.lang.GosuLanguage;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatementList;
import gw.plugin.ij.lang.psi.api.types.IGosuCodeReferenceElement;
import gw.plugin.ij.lang.psi.impl.*;
import gw.plugin.ij.lang.psi.impl.expressions.*;
import gw.plugin.ij.lang.psi.impl.search.GosuMethodSuperSearcher;
import gw.plugin.ij.lang.psi.impl.statements.*;
import gw.plugin.ij.lang.psi.impl.statements.params.GosuParameterImpl;
import gw.plugin.ij.lang.psi.impl.statements.params.GosuParameterListImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.*;
import gw.plugin.ij.lang.psi.impl.statements.typedef.members.GosuMethodBaseImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.members.GosuMethodImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.members.ThrowsReferenceList;
import gw.plugin.ij.lang.psi.impl.types.GosuTypeParameterListImpl;
import gw.plugin.ij.lang.psi.impl.types.GosuTypeVariableExtendsListImpl;
import gw.plugin.ij.lang.psi.impl.types.GosuTypeVariableImpl;
import gw.plugin.ij.lang.psi.impl.types.GosuTypeVariableListImpl;
import gw.plugin.ij.lang.psi.stubs.impl.GosuFieldStubImpl;
import gw.plugin.ij.lang.psi.stubs.impl.GosuMethodStubImpl;
import gw.plugin.ij.lang.psi.stubs.impl.GosuTypeDefinitionStubImpl;
import org.mockito.Mockito;

import java.util.List;

/**
 * Created by tp_1996397 on 6/25/2015.
 */
public class CollectionWhereFirstTest extends InspectionsTestBase {
    public void testCollectionWhereFirst() {
//        final IGosuStatementList startElement = Common.searchForElementOfType(this.gosuSourceCode, GosuStatementListImpl.class);
//
        ProblemsHolder problemsHolderMock = Mockito.mock(ProblemsHolder.class);
//        ((GosuElementVisitor)new CollectionWhereFirst().buildVisitor(problemsHolderMock, false)).visitStatementList(startElement);

//        final GosuBeanMethodCallExpressionImpl beanMethod1 = Common.searchForElementOfType(this.gosuSourceCode, GosuBeanMethodCallExpressionImpl.class);
//        ((GosuElementVisitor)new CollectionWhereFirst().buildVisitor(problemsHolderMock, false)).visitBeanMethodCallExpression(beanMethod1);


        final List<PsiElement> beanMethods = Common.searchForElementsOfType(this.gosuSourceCode, GosuBeanMethodCallExpressionImpl.class);
        for (PsiElement beanMethod : beanMethods) {
            if ( beanMethod instanceof GosuBeanMethodCallExpressionImpl ) {
                ((GosuElementVisitor)new CollectionWhereFirst().buildVisitor(problemsHolderMock, false)).visitBeanMethodCallExpression((GosuBeanMethodCallExpressionImpl)beanMethod);
            }
        }

//        System.out.println(problemsHolderMock.hasResults());
//        System.out.println(problemsHolderMock.getResults().size());
    }
}
