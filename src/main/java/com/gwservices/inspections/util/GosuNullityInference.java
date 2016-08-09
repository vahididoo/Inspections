/*
 * Copyright 2000-2014 JetBrains s.r.o.
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
package com.gwservices.inspections.util;

import com.intellij.codeInsight.*;
import com.intellij.codeInspection.dataFlow.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.containers.*;
import gw.gosu.ij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author peter
 */
public class GosuNullityInference extends NullityInference {

    public static Nullness inferNullity(final PsiMethod method) {
 /*   if (!InferenceFromSourceUtil.shouldInferFromSource(method)) {
      return Nullness.UNKNOWN;
    }*/

        PsiType type = method.getReturnType();
        if (type == null || type instanceof PsiPrimitiveType) {
            return Nullness.UNKNOWN;
        }

        return CachedValuesManager.getCachedValue(method, new CachedValueProvider<Nullness>() {
            @Nullable
            @Override
            public Result<Nullness> compute() {
                Nullness result = RecursionManager.doPreventingRecursion(method, true, new Computable<Nullness>() {
                    @Override
                    public Nullness compute() {
                        return doInferNullity(method);
                    }
                });
                if (result == null) result = Nullness.UNKNOWN;
                return Result.create(result, method, PsiModificationTracker.JAVA_STRUCTURE_MODIFICATION_COUNT);
            }
        });
    }

    @NotNull
    private static Nullness doInferNullity(PsiMethod method) {
        PsiCodeBlock body = method.getBody();
        if (body != null) {
            final AtomicBoolean hasErrors = new AtomicBoolean();
            final AtomicBoolean hasNotNulls = new AtomicBoolean();
            final AtomicBoolean hasNulls = new AtomicBoolean();
            final AtomicBoolean hasUnknowns = new AtomicBoolean();
            final Set<PsiMethod> delegates = ContainerUtil.newLinkedHashSet();
            body.accept(new GosuRecursiveElementWalkingVisitor() {
                @Override
                public void visitReturnStatement(PsiReturnStatement statement) {
                    PsiExpression value = statement.getReturnValue();
                    if (value == null) {
                        hasErrors.set(true);
                    } else if (value instanceof PsiLiteralExpression) {
                        if (value.textMatches(PsiKeyword.NULL)) {
                            hasNulls.set(true);
                        } else {
                            hasNotNulls.set(true);
                        }
                    } else if (value instanceof PsiLambdaExpression || value.getType() instanceof PsiPrimitiveType) {
                        hasNotNulls.set(true);
                    } else if (containsNulls(value)) {
                        hasNulls.set(true);
                    } else if (value instanceof PsiMethodCallExpression) {
                        PsiMethod target = ((PsiMethodCallExpression) value).resolveMethod();
                        if (target == null) {
                            hasUnknowns.set(true);
                        } else {
                            delegates.add(target);
                        }
                    }
                    if (value instanceof PsiReferenceExpression) {
                        PsiElement resolve = value.getReference().resolve();
                        if (resolve instanceof PsiLocalVariable) {
                            Nullness nullness = inferNullity((PsiLocalVariable) resolve);
                            if (nullness.equals(Nullness.NULLABLE)) {
                                hasNulls.set(true);
                            } else if (nullness.equals(Nullness.NOT_NULL)) {
                                hasNotNulls.set(true);
                            } else {
                                hasUnknowns.set(true);
                            }
                        }
                    } else {
                        hasUnknowns.set(true);
                    }
                    super.visitReturnStatement(statement);
                }

                private boolean containsNulls(PsiExpression value) {
                    if (value instanceof PsiConditionalExpression) {
                        return containsNulls(((PsiConditionalExpression) value).getElseExpression()) || containsNulls
                                (((PsiConditionalExpression) value).getThenExpression());
                    }
                    if (value instanceof PsiParenthesizedExpression) {
                        return containsNulls(((PsiParenthesizedExpression) value).getExpression());
                    }
                    return value instanceof PsiLiteralExpression && value.textMatches(PsiKeyword.NULL);
                }

                @Override
                public void visitClass(PsiClass aClass) {
                }

                @Override
                public void visitLambdaExpression(PsiLambdaExpression expression) {
                }

                @Override
                public void visitErrorElement(PsiErrorElement element) {
                    hasErrors.set(true);
                    super.visitErrorElement(element);
                }

            });

            if (hasNulls.get()) {
                return suppressNullable(method) ? Nullness.UNKNOWN : Nullness.NULLABLE;
            }

            if (hasErrors.get() || hasUnknowns.get() || delegates.size() > 1) {
                return Nullness.UNKNOWN;
            }

            if (delegates.size() == 1) {
                if (NullableNotNullManager.isNotNull(delegates.iterator().next())) {
                    return Nullness.NOT_NULL;
                }
                return Nullness.UNKNOWN;
            }

            if (hasNotNulls.get()) {
                return Nullness.NOT_NULL;
            }

        }
        return Nullness.UNKNOWN;
    }

    static boolean suppressNullable(PsiMethod method) {
        if (method.getParameterList().getParametersCount() == 0) return false;

        for (MethodContract contract : getMethodContracts(method)) {
            if (contract.returnValue == MethodContract.ValueConstraint.NULL_VALUE) {
                return true;
            }
        }
        return false;
    }

    static List<MethodContract> getMethodContracts(@NotNull final PsiMethod method) {
        return CachedValuesManager.getCachedValue(method, new CachedValueProvider<List<MethodContract>>() {
            @Nullable
            @Override
            public Result<List<MethodContract>> compute() {
                final PsiAnnotation contractAnno = findContractAnnotation(method);
                if (contractAnno != null) {
                    String text = AnnotationUtil.getStringAttributeValue(contractAnno, null);
                    if (text != null) {
                        try {
                            final int paramCount = method.getParameterList().getParametersCount();
                            List<MethodContract> applicable = ContainerUtil.filter(MethodContract.parseContract(text)
                                    , new Condition<MethodContract>() {
                                @Override
                                public boolean value(MethodContract contract) {
                                    return contract.arguments.length == paramCount;
                                }
                            });
                            return Result.create(applicable, contractAnno, method, PsiModificationTracker
                                    .JAVA_STRUCTURE_MODIFICATION_COUNT);
                        } catch (Exception ignored) {
                        }
                    }
                }
                return Result.create(Collections.<MethodContract>emptyList(), method, PsiModificationTracker
                        .JAVA_STRUCTURE_MODIFICATION_COUNT);
            }
        });
    }

    @Nullable
    public static PsiAnnotation findContractAnnotation(@NotNull PsiMethod method) {
        return AnnotationUtil.findAnnotationInHierarchy(method, Collections.singleton(Contract.class.getName()));
    }

    public static Nullness inferNullity(PsiLocalVariable resolve) {
        if (resolve.getInitializer() == null || "null".equalsIgnoreCase(resolve.getInitializer().getText())) {
            return Nullness.NULLABLE;
        }
        PsiReference reference = resolve.getInitializer().getReference();
        if (reference != null) {
            PsiElement resolvedRereference = reference.resolve();
            if (resolvedRereference instanceof PsiMethodCallExpression) {
                PsiMethod method = (PsiMethod) ((PsiMethodCallExpression) resolvedRereference).getMethodExpression()
                                                                                              .resolve();
                return doInferNullity(method);
            } else if (resolvedRereference instanceof PsiLocalVariable) {
                return inferNullity((PsiLocalVariable) resolvedRereference);
            }
        }
        if (resolve.getInitializer() instanceof PsiMethodCallExpression) {
            PsiMethod psiMethod = ((PsiMethodCallExpression) resolve.getInitializer()).resolveMethod();
            return inferNullity(psiMethod);
        }
        return Nullness.UNKNOWN;
    }
}
