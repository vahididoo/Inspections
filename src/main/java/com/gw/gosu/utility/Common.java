
package com.gw.gosu.utility;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.tree.IElementType;
import gw.internal.gosu.parser.ExecutionEnvironment;
import gw.internal.gosu.parser.expressions.BeanMethodCallExpression;
import gw.lang.parser.expressions.IBeanMethodCallExpression;
import gw.plugin.ij.formatting.GosuCodeStyleSettings;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.GosuElementType;
import gw.plugin.ij.lang.GosuLanguage;
import gw.plugin.ij.lang.parser.GosuElementTypes;
import gw.plugin.ij.lang.parser.GosuRawPsiElement;
import gw.plugin.ij.lang.psi.IGosuFileBase;
import gw.plugin.ij.lang.psi.api.statements.IGosuStatementList;
import gw.plugin.ij.lang.psi.api.statements.IGosuVariable;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuClassDefinition;
import gw.plugin.ij.lang.psi.api.statements.typedef.IGosuMethod;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuBeanMethodCallExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuParenthesizedExpressionImpl;
import gw.plugin.ij.lang.psi.impl.expressions.GosuTypeLiteralImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuDoWhileStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuForEachStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.GosuWhileStatementImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuClassDefinitionImpl;
import gw.plugin.ij.lang.psi.util.GosuPsiParseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class containing helper methods.
 */
public abstract class Common {
    /**
     * Gets the number of columns of the right margin from Intellij api.
     *
     * @param myProject The currently opened project
     * @return The number of spaces used
     */
    public static int getMaxLineLength(final Project myProject) {
        CodeStyleSettings styleSettings = CodeStyleSettingsManager.getSettings(myProject);
        return styleSettings.RIGHT_MARGIN;
    }

    /**
     * Gets the indent settings from Intellij api.
     *
     * @param myProject The currently opened project
     * @param filetype  The type of file being used
     * @return The number of spaces used
     */
    public static int getIndentSize(final Project myProject, final FileType filetype) {
        CodeStyleSettings styleSettings = CodeStyleSettingsManager.getSettings(myProject);
        return styleSettings.getIndentSize(filetype);
    }

    public static boolean spaceBeforeParentheses(final PsiElement elem) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(elem.getProject());
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(GosuLanguage.instance());

        //
        // Firstly, check for tokens
        //
        if (Common.isElementType(elem, GosuElementTypes.TT_if)) {
            return commonSettings.SPACE_BEFORE_IF_PARENTHESES;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_for)) {
            return commonSettings.SPACE_BEFORE_FOR_PARENTHESES;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_while)) {
            return commonSettings.SPACE_BEFORE_WHILE_PARENTHESES;
        }

        //
        // Finally, check all of the interface types
        //
        PsiElement parent = elem.getParent();

        if (parent instanceof GosuIfStatementImpl) {
            return commonSettings.SPACE_BEFORE_IF_PARENTHESES;
        }
        else if (parent instanceof GosuForEachStatementImpl) {
            return commonSettings.SPACE_BEFORE_FOR_PARENTHESES;
        }
        else if (parent instanceof IGosuMethod) {
            return commonSettings.SPACE_BEFORE_METHOD_PARENTHESES;
        }
        else if (parent instanceof GosuWhileStatementImpl) {
            return commonSettings.SPACE_BEFORE_WHILE_PARENTHESES;
        }
        else if (parent instanceof GosuParenthesizedExpressionImpl) {
            return true;
        }

        return false;
    }

    public static boolean spacesWithinParentheses(final PsiElement elem) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(elem.getProject());
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(GosuLanguage.instance());

        //
        // Firstly, check for tokens
        //
        if (Common.isElementType(elem, GosuElementTypes.TT_if)) {
            return commonSettings.SPACE_WITHIN_IF_PARENTHESES;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_for)) {
            return commonSettings.SPACE_WITHIN_FOR_PARENTHESES;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_while)) {
            return commonSettings.SPACE_WITHIN_WHILE_PARENTHESES;
        }

        //
        // Finally, check all of the interface types
        //
        PsiElement parent = elem.getParent();

        if (parent instanceof GosuIfStatementImpl) {
            return commonSettings.SPACE_WITHIN_IF_PARENTHESES;
        }
        else if (parent instanceof GosuForEachStatementImpl) {
            return commonSettings.SPACE_WITHIN_FOR_PARENTHESES;
        }
        else if (parent instanceof IGosuMethod) {
            return commonSettings.SPACE_WITHIN_METHOD_PARENTHESES;
        }
        else if (parent instanceof GosuWhileStatementImpl) {
            return commonSettings.SPACE_WITHIN_WHILE_PARENTHESES;
        }

        return false;
    }

    public static boolean spaceAroundOperators(final PsiElement elem) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(elem.getProject());
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(GosuLanguage.instance());

        //
        // Firstly, check for tokens
        //
        if (Common.isElementType(elem, GosuElementTypes.TT_OP_assign)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_and)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_closure)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_divide)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_logical_and)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_minus)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_modulo)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_multiply)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_plus)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_shift_left)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_shift_right)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_shift_right_unsigned)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assign_xor)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assing_logical_or)
                || Common.isElementType(elem, GosuElementTypes.TT_OP_assing_or)) {

            return commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_bitwise_and)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_bitwise_or)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_bitwise_xor)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_not_bitwise)) {

            return commonSettings.SPACE_AROUND_BITWISE_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_plus)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_minus)) {

            return commonSettings.SPACE_AROUND_ADDITIVE_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_not_equals)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_equals)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_not_equals_for_losers)) {

            return commonSettings.SPACE_AROUND_EQUALITY_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_logical_and)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_logical_or)) {

            return commonSettings.SPACE_AROUND_LOGICAL_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_shift_left)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_shift_right)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_shift_right_unsigned)) {

            return commonSettings.SPACE_AROUND_SHIFT_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_multiply)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_divide)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_modulo)) {

            return commonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_increment)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_decrement)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_plus)
                       || Common.isElementType(elem, GosuElementTypes.TT_OP_minus)) {

            return commonSettings.SPACE_AROUND_UNARY_OPERATOR;

        }
        return false;
    }

    public static boolean spaceBeforeTernaryOperators(final PsiElement elem) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(elem.getProject());
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(GosuLanguage.instance());

        //
        // Firstly, check for tokens
        //
        if (Common.isElementType(elem, GosuElementTypes.TT_OP_colon)) {
            return commonSettings.SPACE_BEFORE_COLON;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_question)) {
            return commonSettings.SPACE_BEFORE_QUEST;

        }
        return false;
    }

    public static boolean spaceAfterTernaryOperators(final PsiElement elem) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(elem.getProject());
        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(GosuLanguage.instance());

        //
        // Firstly, check for tokens
        //
        if (Common.isElementType(elem, GosuElementTypes.TT_OP_colon)) {
            return commonSettings.SPACE_AFTER_COLON;

        } else if (Common.isElementType(elem, GosuElementTypes.TT_OP_question)) {
            return commonSettings.SPACE_AFTER_QUEST;

        }
        return false;
    }

    /**
     * Finds the right indent for any input element in program, measure the distance betweee it and the first previous BreakLine element.
     * @param elem The element we want to check the indent of
     * @return The amount of indent
     */
    public static int getIndent(final PsiElement elem) {
        int indent = 1;
        PsiElement current = elem;
        while (current.getPrevSibling() == null) {
            current = current.getParent();
        }
        current = current.getPrevSibling();


        if (!elem.getText().startsWith("\n")) {
            while (!current.textContains('\n')) {
                indent += current.getTextLength();

                while (current.getPrevSibling() == null) {
                    current = current.getParent();
                }
                current = current.getPrevSibling();

                //We need to look at the element before the current one that doesn't contain a newline, because we want to get all
                //the indentation that occurs before we hit a newline character
                while (!(current instanceof PsiWhiteSpaceImpl) && current.textContains('\n')) {
                    current = current.getLastChild();
                }
            }
        }
        return indent + getStartOfLineIndent(current);
    }

    /**
     * Checks if an element is one of the given types.
     *
     * @param elem  The element to be checked
     * @param types The types to be compared to the elem's type
     * @return True if at least one match is found
     */
    public static boolean isElementType(final PsiElement elem, final IElementType... types) {
        if (elem != null) {

            for (IElementType type : types) {
                // TODO: Should this be .equals?
                if (elem.getNode().getElementType() == type) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if this is the last non-whitespace/non-comment on the line.
     *
     * @param elem The element that is checked
     * @return True if the element has no trailing tokens
     */
    public static boolean hasNoTrailingTokens(final PsiElement elem) {
        PsiElement current = elem.getNextSibling();
        while (current != null
                && !(current instanceof PsiWhiteSpace && current.textContains('\n'))) {
            if (!(current instanceof GosuCommentImpl || current instanceof PsiWhiteSpace)) {
                return false;
            }
            current = current.getNextSibling();
        }

        return true;
    }

    /**
     * Gets the intellij style setting of whether a space should occur before a left brace.
     * @param elem The element that we are checking
     * @return True if a whitespace is required
     */
    public static boolean spaceBeforeLeftBrace(final PsiElement elem) {
        CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(elem.getProject());

        CommonCodeStyleSettings commonSettings = settings.getCommonSettings(GosuLanguage.instance());
        GosuCodeStyleSettings gosuSettings = settings.getCustomSettings(GosuCodeStyleSettings.class);

        //
        // Firstly, check for tokens
        //
        if (Common.isElementType(elem, GosuElementTypes.TT_else)) {
            return commonSettings.SPACE_BEFORE_ELSE_LBRACE;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_catch)) {
            return commonSettings.SPACE_BEFORE_CATCH_LBRACE;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_try)) {
            return commonSettings.SPACE_BEFORE_TRY_LBRACE;
        } else if (Common.isElementType(elem, GosuElementTypes.TT_using)) {
            return gosuSettings.SPACE_BEFORE_USING_LBRACE;
        }

        //
        // Finally, check all of the interface types
        //
        PsiElement parent = elem.getParent();

        if (parent instanceof GosuIfStatementImpl) {
            return commonSettings.SPACE_BEFORE_IF_LBRACE;
        }
        else if (parent instanceof GosuForEachStatementImpl) {
            return commonSettings.SPACE_BEFORE_FOR_LBRACE;
        }
        else if (parent instanceof IGosuMethod) {
            return commonSettings.SPACE_BEFORE_METHOD_LBRACE;
        }
        else if (parent instanceof GosuWhileStatementImpl) {
            return commonSettings.SPACE_BEFORE_WHILE_LBRACE;
        }
        else if (parent instanceof GosuDoWhileStatementImpl) {
            return commonSettings.SPACE_BEFORE_DO_LBRACE;
        }
        else if ((parent instanceof IGosuClassDefinition)
                // Top-level element in a file is also a class/enhancement
                || (parent instanceof IGosuFileBase)) {
            return commonSettings.SPACE_BEFORE_CLASS_LBRACE;
        }


        return false;
    }

    /**
     * Checks if the current block is empty.
     * @param elem The element containing the block of code
     * @return True if the block of code is empty
     */
    public static boolean isBlockEmpty(final PsiElement elem) {
        PsiElement current = elem;

        if (current instanceof IGosuStatementList //If it's a block containing statements, then it's an IGosuStatementList
                || current instanceof GosuRawPsiElement) //If it's an empty block, then it's a GosuRawPsiElement
        {
            current = current.getFirstChild();

            //A block is empty if it consists of a single semicolon
            if (Common.isElementType(current, GosuElementTypes.TT_OP_semicolon)) {
                return true;
            } else if (Common.isElementType(current, GosuElementTypes.TT_OP_brace_left)) { //A block is also empty if it has nothing between its curly brackets
                current = current.getNextSibling();   //Skip over the left curly brace

                while (current instanceof PsiWhiteSpaceImpl || current instanceof PsiCommentImpl) { //Skip over whitespace and comments
                    current = current.getNextSibling();
                }

                if (Common.isElementType(current, GosuElementTypes.TT_OP_brace_right)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Gets the number of spaces at the start of the line the element is on,
     * regardless of whether the given element is at the start of the line.
     *
     * @param elem Any element in the line we are checking
     * @return The number of spaces occurring at the start of this line
     */
    public static int getStartOfLineIndent(final PsiElement elem) {
        PsiElement current = elem;
        while (current != null && !(current instanceof PsiWhiteSpace && current.textContains('\n'))) {
            if (current.getPrevSibling() == null) {
                current = current.getParent();
                if (current instanceof GosuClassDefinitionImpl && current.getPrevSibling() == null) { //We're at the top of the tree
                    return 0;
                }
            }
            current = current.getPrevSibling();
        }

        if (current == null) {
            return 0;
        }

        String[] lines = current.getText().split("\n");
        if (current.getText().charAt(current.getTextLength() - 1) == '\n') { //If the last character is a newline
            return 0;
        } else {
            return lines[lines.length - 1].length();
        }
    }

    /**
     * Gets the number of spaces at the start of the line the element is on,
     * regardless of whether the given element is at the start of the line.
     *
     * @param elem Any element in the line we are checking
     * @return The number of spaces occurring at the start of this line
     */
    public static String getStartOfLineIndentStr(final PsiElement elem) {
        PsiElement current = elem;
        while (current != null && !(current instanceof PsiWhiteSpace && current.textContains('\n'))) {
            if (current.getPrevSibling() == null) {
                current = current.getParent();
                if (current instanceof GosuClassDefinitionImpl && current.getPrevSibling() == null) { //We're at the top of the tree
                    return "";
                }
            }
            current = current.getPrevSibling();
        }

        if (current == null) {
            return "";
        }

        String[] lines = current.getText().split("\n");
        if (current.getText().charAt(current.getTextLength() - 1) == '\n') { //If the last character is a newline
            return "";
        } else {
            return lines[lines.length - 1];
        }
    }

    /**
     * Gets the part of the for/while that executes when the condition is true.
     *
     * @param elem The element consisting of the entire for/while statement
     * @return The body element of the for/while
     */
    public static PsiElement getConditionalBody(final PsiElement elem) {
        PsiElement current = elem.getLastChild();

        if (current instanceof GosuTypeLiteralImpl || Common.isElementType(current, GosuElementTypes.TT_OP_paren_right)) {
            return null;
        }

        if (current instanceof IGosuStatementList) {
            return current.getFirstChild();
        } else {
            return current;
        }
    }

    /**
     * Gets the part of the if statement that executes when the condition is true.
     *
     * @param elem The element consisting of the entire if statement
     * @return The body element of the for/while
     */
    public static PsiElement getIfBody(final GosuIfStatementImpl elem) {
        PsiElement current = elem.getFirstChild();
        while (current != null && !Common.isElementType(current, GosuElementTypes.TT_OP_paren_right)) {
            current = current.getNextSibling();
        }

        if (current == null) {
            return null;
        }

        //Skip over right parenthesis
        current = current.getNextSibling();

        while (current instanceof PsiWhiteSpace || current instanceof GosuCommentImpl) { //Skip over the whitespace and comments
            current = current.getNextSibling();
        }

        if (current instanceof IGosuStatementList) {
            return current.getFirstChild();
        } else {
            return current;
        }
    }

    /**
     * Skips to the next line, but ignores children elements, so it may skip a few lines.
     *
     * @param elem The current element
     * @return The starting element of the new line (never whitespace)
     */
    public static PsiElement advanceToNextLineOuter(final PsiElement elem) {
        PsiElement current = elem;

        //Keep going until we see a newline character
        while (!(current instanceof PsiWhiteSpace && current.textContains('\n'))) {
            if (current == null) {
                return null;
            }

            current = current.getNextSibling();
        }
        return current.getNextSibling(); //Skip over the whitespace
    }

    /**
     * Search for an element of the given type in the parsing tree. The first element of the tree that matches is
     * returned. The search is width first.
     *
     * @param elementTree the parsing tree
     * @param elementType the class of the element to search for
     * @param <T> the type of the element to search for
     * @return return the first element of the tree found or null
     */
    public static <T extends PsiElement> T searchForElementOfType(PsiElement elementTree, Class<T> elementType) {
        if (elementTree == null) {
            return null;
        } else if (elementType.isInstance(elementTree)) {
            return elementType.cast(elementTree);
        } else {
            PsiElement[] children = elementTree.getChildren();
            if (children != null) {
                for (PsiElement child : children) {
                    T result = searchForElementOfType(child, elementType);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Search for an element of the given type in the parsing tree. The first element of the tree that matches is
     * returned. The search is width first.
     *
     * @param elementTree the parsing tree
     * @param elementType the class of the element to search for
     * @param <T> the type of the element to search for
     * @return return the first element of the tree found or null
     */
    public static <T extends PsiElement> List<PsiElement> searchForElementsOfType(PsiElement elementTree, Class<T> elementType) {
        List<PsiElement> result = new ArrayList<>();
        searchForElementsOfTypeInternal(elementTree, elementType, result);

        return result;
    }

    /**
     * Generate a parsing tree from the provided gosu code.
     *
     * @param programCode a string representing a gosu program
     * @param manager the PsiManager instance
     * @return a parsing tree built based on the provided gosu code
     */
    public static GosuProgramFileImpl createGosuProgramFile(String programCode, PsiManager manager) {
        return (GosuProgramFileImpl) GosuPsiParseUtil
                .parseProgramm(programCode, manager, ExecutionEnvironment.instance().getGlobalModule());
    }

    public static boolean isCaughtExceptionIdentifier(final IGosuVariable variable) {
        return isElementType(variable.getParent(), GosuElementTypes.ELEM_TYPE_CatchClause);
    }

    private static <T extends PsiElement> void searchForElementsOfTypeInternal(
            PsiElement elementTree, Class<T> elementType, List<PsiElement> results) {
        if (elementTree != null && elementType.isInstance(elementTree)) {
            results.add(elementType.cast(elementTree));
        } else {
            PsiElement[] children = elementTree.getChildren();
            if (children != null) {
                for (PsiElement child : children) {
                    searchForElementsOfTypeInternal(child, elementType, results);
                }
            }
        }
    }

    public static PsiElement getFirstApparenceOfElementType(PsiElement element, String methodName) {
        if (element == null) {
            return null;
//        } else if ((element instanceof BeanMethodCallExpression) &&
//                  (((GosuBeanMethodCallExpressionImpl) element).getName() == methodName)) {
//            return element;
        } else {
            PsiElement[] children = element.getChildren();
            if (children != null) {
                for (PsiElement child : children) {
                    PsiElement result = getFirstApparenceOfElementType(child, methodName);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    public static PsiElement getNextNonWhiteSibling(PsiElement element) {
        while (element!=null && element.getNode().getElementType()==GosuElementTypes.TT_WHITESPACE) {
            element = element.getNextSibling();
        }
        return element;
    }
}
