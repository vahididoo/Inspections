package com.gw.gosu.inspections.layout;

import com.gw.gosu.common.InspectionsTestBase;
import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.layout.CurlyBracketsQuickFix;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import gw.plugin.ij.lang.GosuTokenImpl;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class CurlyBracketsTest extends InspectionsTestBase {

    public void testCurlyBracketsInspection() {
        final String inputString = "if(true) print(\"boo\")";

        final GosuProgramFileImpl programFile = Common.createGosuProgramFile(inputString, this.getPsiManager());
        final PsiElement startElement = Common.searchForElementOfType(programFile, GosuIfStatementImpl.class);

        final GosuTokenImpl token = (GosuTokenImpl) startElement.getNode().getFirstChildNode();

        ProblemsHolder problemsHolderMock = Mockito.mock(ProblemsHolder.class);
        new CurlyBrackets().buildVisitor(problemsHolderMock, false).visitElement(token);

        ArgumentCaptor<CurlyBracketsQuickFix> curlyBracketsCaptor = ArgumentCaptor.forClass(CurlyBracketsQuickFix.class);
        ArgumentCaptor<PsiElement> psiElement = ArgumentCaptor.forClass(PsiElement.class);

        verify(problemsHolderMock, times(1)).registerProblem(
                psiElement.capture(),
                eq(SampleBundle.message("layout.name.curlybrackets")),
                eq(ProblemHighlightType.GENERIC_ERROR_OR_WARNING),
                curlyBracketsCaptor.capture());
    }
}
