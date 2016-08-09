package com.gwservices.quickfixes.gosu.collection;

import com.intellij.codeInsight.intention.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import gw.gosu.ij.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/4/2016.
 */
public class WhereCountFix extends BaseElementAtCaretIntentionAction {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return element.getLanguage().equals(GosuLanguage.INSTANCE);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws
            IncorrectOperationException {



    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return null;
    }
}
