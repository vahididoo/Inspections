package com.gw.gosu.quickfixes.comments;

import com.gw.gosu.common.InspectionsTestBase;
import com.gw.gosu.common.TestUtilities;
import com.gw.gosu.utility.Common;
import com.intellij.psi.PsiElement;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;

import java.util.List;

public class SingleLineCommentsQuickFixTest extends InspectionsTestBase {

    public void testSingleLineCommentsQuickFix() {
        final String inputString =
                        "\n// This is a multi" +
                        "\n// line comment";
        final String expectedString =
                        "\n/*" +
                        "\n This is a multi" +
                        "\n line comment" +
                        "\n*/";

        final String result = this.performSingleLineCommentsQuickFixOnString(inputString);

        assertEquals(expectedString, result);
    }


    private String performSingleLineCommentsQuickFixOnString(String inputString)
    {
        final GosuProgramFileImpl programFile = Common.createGosuProgramFile(inputString, this.getPsiManager());
        final List<PsiElement> elements = Common
                .searchForElementsOfType(programFile, GosuCommentImpl.class);

        TestUtilities.invokeFixInWriteMode(this.getProject(),
                                           programFile,
                                           elements.get(0),
                                           elements.get(elements.size() - 1),
                                           new SingleLineCommentsQuickFix(elements.get(0), elements.get(elements.size() - 1)));

        return programFile.getViewProvider().getDocument().getText();
    }
}
