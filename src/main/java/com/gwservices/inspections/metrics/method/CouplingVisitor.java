/*
 * Copyright 2003-2007 Dave Griffith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gwservices.inspections.metrics.method;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.siyeh.ig.psiutils.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

class CouplingVisitor extends GosuRecursiveElementWalkingVisitor {
    private final PsiMethod m_method;
    private final boolean m_includeJavaClasses;
    private final boolean m_includeLibraryClasses;
    private final Set<String> m_dependencies = new HashSet<String>(10);
    private int m_inClass;

    CouplingVisitor(@NotNull PsiMethod method, boolean includeJavaClasses, boolean includeLibraryClasses) {
        m_method = method;
        m_includeJavaClasses = includeJavaClasses;
        m_includeLibraryClasses = includeLibraryClasses;
    }

    @Override
    public void visitVariable(@NotNull PsiVariable variable) {
        super.visitVariable(variable);
        final PsiType type = variable.getType();
        addDependency(type);
    }

    @Override
    public void visitMethod(@NotNull PsiMethod method) {
        super.visitMethod(method);
        final PsiType returnType = method.getReturnType();
        addDependency(returnType);
        addDependenciesForThrowsList(method);
    }

    private void addDependenciesForThrowsList(PsiMethod method) {
        final PsiReferenceList throwsList = method.getThrowsList();
        final PsiClassType[] throwsTypes = throwsList.getReferencedTypes();
        for (PsiClassType throwsType : throwsTypes) {
            addDependency(throwsType);
        }
    }

    @Override
    public void visitNewExpression(@NotNull PsiNewExpression exp) {
        super.visitNewExpression(exp);
        final PsiType classType = exp.getType();
        addDependency(classType);
    }

    @Override
    public void visitClassObjectAccessExpression(PsiClassObjectAccessExpression exp) {
        super.visitClassObjectAccessExpression(exp);
        final PsiTypeElement operand = exp.getOperand();
        addDependency(operand);
    }

    @Override
    public void visitClass(@NotNull PsiClass aClass) {
        final int wasInClass = m_inClass++;
        if (wasInClass == 0) {
            super.visitClass(aClass);
        }
        final PsiType[] superTypes = aClass.getSuperTypes();
        for (PsiType superType : superTypes) {
            addDependency(superType);
        }
    }

    @Override
    protected void elementFinished(@NotNull PsiElement element) {
        super.elementFinished(element);
        if (element instanceof PsiClass) {
            m_inClass--;
        }
    }

    @Override
    public void visitTryStatement(@NotNull PsiTryStatement statement) {
        super.visitTryStatement(statement);
        final PsiParameter[] catchBlockParameters = statement.getCatchBlockParameters();
        for (PsiParameter catchBlockParameter : catchBlockParameters) {
            final PsiType catchType = catchBlockParameter.getType();
            addDependency(catchType);
        }
    }

    @Override
    public void visitTypeIsExpression(GosuPsiTypeIsExpression expression) {
        super.visitTypeIsExpression(expression);
        final PsiTypeElement checkType = expression.getCheckType();
        addDependency(checkType);
    }

    @Override
    public void visitTypeAsExpression(PsiTypeCastExpression expression) {
        super.visitTypeAsExpression(expression);
        final PsiTypeElement castType = expression.getCastType();
        addDependency(castType);
    }

    private void addDependency(PsiTypeElement typeElement) {
        if (typeElement == null) {
            return;
        }
        final PsiType type = typeElement.getType();
        addDependency(type);
    }

    private void addDependency(PsiType type) {
        if (type == null) {
            return;
        }
        final PsiType baseType = type.getDeepComponentType();
        if (ClassUtils.isPrimitive(type)) {
            return;
        }
        final PsiClass containingClass = m_method.getContainingClass();
        if (containingClass == null) {
            return;
        }
        final String qualifiedName = containingClass.getQualifiedName();
        if (qualifiedName == null) {
            return;
        }
        if (baseType.equalsToText(qualifiedName)) {
            return;
        }
        @NonNls final String baseTypeName = baseType.getCanonicalText();
        if (!m_includeJavaClasses && (baseTypeName.startsWith("java.") || baseTypeName.startsWith("javax."))) {
            return;
        }
        if (StringUtil.startsWithConcatenation(baseTypeName, qualifiedName, ".")) {
            return;
        }
        if (!m_includeLibraryClasses) {
            final Project project = m_method.getProject();
            final GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
            final PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(baseTypeName, searchScope);
            if (aClass == null) {
                return;
            }
            if (LibraryUtil.classIsInLibrary(aClass)) {
                return;
            }
        }
        m_dependencies.add(baseTypeName);
    }

    int getNumDependencies() {
        return m_dependencies.size();
    }
}
