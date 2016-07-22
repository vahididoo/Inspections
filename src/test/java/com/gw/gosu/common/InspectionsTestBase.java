package com.gw.gosu.common;

import com.gw.gosu.utility.Common;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import gw.internal.gosu.parser.ExecutionEnvironment;
import gw.lang.init.GosuPathEntry;
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class InspectionsTestBase extends LightCodeInsightFixtureTestCase {
    public static final String testDataDir = "testData/";

    protected GosuProgramFileImpl gosuSourceCode;

    public void setUp() throws Exception {
        super.setUp();
        ExecutionEnvironment.instance().initializeDefaultSingleModule(new ArrayList<GosuPathEntry>());
        setupTestCodeSource();
    }

    /**
     * Follow naming convention of
     * (1) Rule class name: Rule.java
     * (2) Rule test class name: RuleTest.java
     * (3) Rule test data (a gosu class) name: RuleCode.gosu
     *
     * @throws Exception
     */
    void setupTestCodeSource() throws Exception {
        String testClassName = this.getClass().getSimpleName();
        System.out.println("Test Class Name is:" + testClassName);

        String gosuSourceCodeFileName = testDataDir + testClassName.substring(0, testClassName.length()-4) + "Code.gs";
        System.out.println("Test Gosu Code Class File is:" + gosuSourceCodeFileName);

        String gosuProgramString = new String(Files.readAllBytes(Paths.get(gosuSourceCodeFileName)));

        gosuSourceCode = Common.createGosuProgramFile(gosuProgramString, this.getPsiManager());
    }

    protected void listAllPossibleElement() {

        PsiElement variableElement = Common.searchForElementOfType(this.gosuSourceCode, GosuVariableImpl.class);
        PsiElement ele1 = Common.searchForElementOfType(this.gosuSourceCode, GosuAnnotationExpressionImpl.class);
        PsiElement ele2 = Common.searchForElementOfType(this.gosuSourceCode, GosuBeanMethodCallExpressionImpl.class);
        PsiElement ele3 = Common.searchForElementOfType(this.gosuSourceCode, GosuBlockExpressionImpl.class);
        PsiElement ele4 = Common.searchForElementOfType(this.gosuSourceCode, GosuDirectiveExpressionImpl.class);
        PsiElement ele5 = Common.searchForElementOfType(this.gosuSourceCode, GosuExpressionListImpl.class);
        PsiElement ele6 = Common.searchForElementOfType(this.gosuSourceCode, GosuFeatureLiteralExpressionImpl.class);
        PsiElement ele7 = Common.searchForElementOfType(this.gosuSourceCode, GosuFieldAccessExpressionImpl.class);
        PsiElement ele8 = Common.searchForElementOfType(this.gosuSourceCode, GosuIdentifierExpressionImpl.class);
        PsiElement ele9 = Common.searchForElementOfType(this.gosuSourceCode, GosuIdentifierImpl.class);
        PsiElement ele10 = Common.searchForElementOfType(this.gosuSourceCode, GosuMemberExpansionExpressionImpl.class);
        PsiElement ele11 = Common.searchForElementOfType(this.gosuSourceCode, GosuMethodCallExpressionImpl.class);
        PsiElement ele12 = Common.searchForElementOfType(this.gosuSourceCode, GosuNameInDeclarationImpl.class);
        PsiElement ele13 = Common.searchForElementOfType(this.gosuSourceCode, GosuNewExpressionImpl.class);
        PsiElement ele14 = Common.searchForElementOfType(this.gosuSourceCode, GosuParenthesizedExpressionImpl.class);
        PsiElement ele15 = Common.searchForElementOfType(this.gosuSourceCode, GosuPropertyMemberAccessExpressionImpl.class);
        PsiElement ele16 = Common.searchForElementOfType(this.gosuSourceCode, GosuReferenceExpressionImpl.class);
        PsiElement ele17 = Common.searchForElementOfType(this.gosuSourceCode, GosuStringLiteralImpl.class);
        PsiElement ele18 = Common.searchForElementOfType(this.gosuSourceCode, GosuTypeAsExpressionImpl.class);
        PsiElement ele19 = Common.searchForElementOfType(this.gosuSourceCode, GosuTypeLiteralImpl.class);
        PsiElement ele20 = Common.searchForElementOfType(this.gosuSourceCode, GosuParameterImpl.class);
        PsiElement ele21 = Common.searchForElementOfType(this.gosuSourceCode, GosuParameterListImpl.class);
        PsiElement ele22 = Common.searchForElementOfType(this.gosuSourceCode, GosuMethodBaseImpl.class);
        PsiElement ele23 = Common.searchForElementOfType(this.gosuSourceCode, GosuMethodImpl.class);
        PsiElement ele24 = Common.searchForElementOfType(this.gosuSourceCode, ThrowsReferenceList.class);
        PsiElement ele25 = Common.searchForElementOfType(this.gosuSourceCode, GosuAnnotationDefinitionImpl.class);
        PsiElement ele26 = Common.searchForElementOfType(this.gosuSourceCode, GosuAnonymousClassDefinitionImpl.class);
        PsiElement ele27 = Common.searchForElementOfType(this.gosuSourceCode, GosuClassDefinitionImpl.class);
        PsiElement ele28 = Common.searchForElementOfType(this.gosuSourceCode, GosuEnhancementDefinitionImpl.class);
        PsiElement ele29 = Common.searchForElementOfType(this.gosuSourceCode, GosuEnumConstantImpl.class);
        PsiElement ele30 = Common.searchForElementOfType(this.gosuSourceCode, GosuEnumDefinitionImpl.class);
        PsiElement ele31 = Common.searchForElementOfType(this.gosuSourceCode, GosuExtendsClauseImpl.class);
        PsiElement ele32 = Common.searchForElementOfType(this.gosuSourceCode, GosuImplementsClauseImpl.class);
        PsiElement ele33 = Common.searchForElementOfType(this.gosuSourceCode, GosuInterfaceDefinitionImpl.class);
        PsiElement ele34 = Common.searchForElementOfType(this.gosuSourceCode, GosuReferenceListImpl.class);
        PsiElement ele35 = Common.searchForElementOfType(this.gosuSourceCode, GosuSyntheticClassDefinitionImpl.class);
        PsiElement ele36 = Common.searchForElementOfType(this.gosuSourceCode, AbstractStatementWithLocalDeclarationsImpl.class);
        PsiElement ele37 = Common.searchForElementOfType(this.gosuSourceCode, GosuAssignmentStatementImpl.class);
        PsiElement ele38 = Common.searchForElementOfType(this.gosuSourceCode, GosuBaseStatementImpl.class);
        PsiElement ele39 = Common.searchForElementOfType(this.gosuSourceCode, GosuDoWhileStatementImpl.class);
        PsiElement ele40 = Common.searchForElementOfType(this.gosuSourceCode, GosuFieldImpl.class);
        PsiElement ele41 = Common.searchForElementOfType(this.gosuSourceCode, GosuFieldPropertyImpl.class);
        PsiElement ele42 = Common.searchForElementOfType(this.gosuSourceCode, GosuForEachStatementImpl.class);
        PsiElement ele43 = Common.searchForElementOfType(this.gosuSourceCode, GosuIfStatementImpl.class);
        PsiElement ele44 = Common.searchForElementOfType(this.gosuSourceCode, GosuMemberAssignmentStatementImpl.class);
        PsiElement ele45 = Common.searchForElementOfType(this.gosuSourceCode, GosuModifierListImpl.class);
        PsiElement ele46 = Common.searchForElementOfType(this.gosuSourceCode, GosuNotAStatementImpl.class);
        PsiElement ele47 = Common.searchForElementOfType(this.gosuSourceCode, GosuPackageDefinitionImpl.class);
        PsiElement ele48 = Common.searchForElementOfType(this.gosuSourceCode, GosuStatementListImpl.class);
        PsiElement ele49 = Common.searchForElementOfType(this.gosuSourceCode, GosuSyntheticModifierListImpl.class);
        PsiElement ele50 = Common.searchForElementOfType(this.gosuSourceCode, GosuUsesStatementImpl.class);
        PsiElement ele51 = Common.searchForElementOfType(this.gosuSourceCode, GosuUsesStatementListImpl.class);
        PsiElement ele52 = Common.searchForElementOfType(this.gosuSourceCode, GosuUsingStatementImpl.class);
        PsiElement ele53 = Common.searchForElementOfType(this.gosuSourceCode, GosuVariableBaseImpl.class);
        PsiElement ele54 = Common.searchForElementOfType(this.gosuSourceCode, GosuWhileStatementImpl.class);
        PsiElement ele55 = Common.searchForElementOfType(this.gosuSourceCode, GosuTypeParameterListImpl.class);
        PsiElement ele56 = Common.searchForElementOfType(this.gosuSourceCode, GosuTypeVariableExtendsListImpl.class);
        PsiElement ele57 = Common.searchForElementOfType(this.gosuSourceCode, GosuTypeVariableImpl.class);
        PsiElement ele58 = Common.searchForElementOfType(this.gosuSourceCode, GosuTypeVariableListImpl.class);
        PsiElement ele70 = Common.searchForElementOfType(this.gosuSourceCode, AbstractGosuClassFileImpl.class);
        PsiElement ele71 = Common.searchForElementOfType(this.gosuSourceCode, GosuBaseElementImpl.class);
        PsiElement ele72 = Common.searchForElementOfType(this.gosuSourceCode, GosuBlockLightClassReference.class);
        PsiElement ele73 = Common.searchForElementOfType(this.gosuSourceCode, GosuClassFileImpl.class);
        PsiElement ele74 = Common.searchForElementOfType(this.gosuSourceCode, GosuDeclaredElementImpl.class);
        PsiElement ele75 = Common.searchForElementOfType(this.gosuSourceCode, GosuEnhancementFileImpl.class);
        PsiElement ele76 = Common.searchForElementOfType(this.gosuSourceCode, GosuProgramFileImpl.class);
        PsiElement ele77 = Common.searchForElementOfType(this.gosuSourceCode, GosuPsiElementImpl.class);
        PsiElement ele78 = Common.searchForElementOfType(this.gosuSourceCode, GosuScratchpadFileImpl.class);
        PsiElement ele79 = Common.searchForElementOfType(this.gosuSourceCode, GosuTemplateFileImpl.class);
        PsiElement ele80 = Common.searchForElementOfType(this.gosuSourceCode, GosuEnumConstantImpl.class);
        PsiElement ele81 = Common.searchForElementOfType(this.gosuSourceCode, GosuFieldPropertyImpl.class);
        PsiElement ele82 = Common.searchForElementOfType(this.gosuSourceCode, GosuCommentImpl.class);
        PsiElement ele83 = Common.searchForElementOfType(this.gosuSourceCode, GosuTokenImpl.class);

        printElementContent(this.gosuSourceCode.getFirstChild(), 0);
    }

    protected void printElementContent(PsiElement element, int level) {
//        if (element.getText()!=null && element.getText().trim().length()>0) {
        ASTNode node = element.getNode();
        System.out.print("|" + level + "|" + node.getElementType() + "|" + node.getText() + "|\n");
        System.out.print("|" + level + "|" + element.getClass().getSimpleName() + "|" + element.getText()  + "|\n\n");
//        }

        if ("VarStatement".equals(node.getElementType().toString())) {
            int i=0;
        }

        if (element.getChildren().length>0) {
            for (int i=0; i<element.getChildren().length; i++) {
                printElementContent(element.getChildren()[i], level + 1);
            }
        }
    }
}
