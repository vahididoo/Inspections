package com.gw.gosu.common;

import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.psi.impl.*;
import gw.plugin.ij.lang.psi.impl.expressions.*;
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

public class TestUtilities {

    public static void invokeFixInWriteMode(final Project project,
                                            final GosuProgramFileImpl programFile,
                                            final PsiElement startElement,
                                            final LocalQuickFixAndIntentionActionOnPsiElement quickFix) {
        invokeFixInWriteMode(project, programFile, startElement, startElement, quickFix);
    }

    public static void invokeFixInWriteMode(final Project project,
                                            final GosuProgramFileImpl programFile,
                                            final PsiElement startElement,
                                            final PsiElement endElement,
                                            final LocalQuickFixAndIntentionActionOnPsiElement quickFix) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                quickFix.invoke(project, programFile, null, startElement, endElement);
            }
        });
    }
}
