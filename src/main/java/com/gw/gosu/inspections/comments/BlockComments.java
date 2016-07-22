
package com.gw.gosu.inspections.comments;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.quickfixes.comments.BlockCommentsQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import gw.plugin.ij.lang.GosuCommentImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Block Comments Inspection & Quickfix:
 * This plugin is designed to check for single line comment but used in a format of block comments
 *
 * The quickfix will help you re-format into single line comment format: "//"
 */

public class BlockComments extends BaseLocalInspectionTool {

    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("comments.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("comments.fix.tooltip.blockcomments");
    }

    @Override
    public final boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "BlockComments";
    }

    /**
     * Inspection part of code
     * @param holder
     * @param isOnTheFly
     * @return
     */
    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitElement(final PsiElement elem) {
                if (elem instanceof GosuCommentImpl) {
                    GosuCommentImpl token = (GosuCommentImpl) elem;
                    //Check for block comments in general
                    if (token.getElementType().toString().equals("_multiline_comment_")) {
                        String fd = token.getText();
                        //Check for number of lines and separate the entire block comments into multiple line comments
                        String[] lines = fd.split("\r\n|\r|\n");
                        //If the block comment has two lines, then:
                        if (lines.length == 2){
                            int lineOneLength = lines[0].length();
                            //If first line is empty, then:
                            if (lines[0].charAt(lineOneLength- 1) == ' ' || lines[0].charAt(lineOneLength- 1) == '*'){
                                holder.registerProblem(elem, SampleBundle.message("comments.fix.tooltip.blockcomments"),
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new BlockCommentsQuickFix(elem));
                            }
                            //If first line is not empty, then check for second line
                            //If second line only has "*/" or "**/", then give a warning - change block comments to a single line comment
                            else {
                                int numberofNonWhiteSpaceCharacters = 0;
                                int numberofStars = 0;
                                int lineTwoLength = lines[1].length();
                                for (int counter = 0; counter < lineTwoLength; counter++) {
                                    if (!Character.isWhitespace(lines[1].charAt(counter))){
                                        numberofNonWhiteSpaceCharacters++;
                                        //Check for number of stars "*" in second line
                                        if (lines[1].charAt(counter) == '*'){
                                            numberofStars++;
                                        }
                                    }
                                }
                                //If second line has two characters and they are "*/"
                                if (numberofNonWhiteSpaceCharacters == 2){
                                    holder.registerProblem(elem, SampleBundle.message("comments.fix.tooltip.blockcomments"),
                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new BlockCommentsQuickFix(elem));
                                }
                                //If second line has three characters, and they are "**/",
                                //exclude the cases such as three characters but they are "a random character"+"*/"
                                else if (numberofNonWhiteSpaceCharacters == 3 && numberofStars == 2){
                                    holder.registerProblem(elem, SampleBundle.message("comments.fix.tooltip.blockcomments"),
                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new BlockCommentsQuickFix(elem));
                                }
                            }
                        }
                        //If the block comment has 3 lines, then:
                        else if (lines.length == 3){
                            int lineOneLength = lines[0].length();
                            int lineThreeLength = lines[2].length();
                            int numberofNonWhiteSpaceCharactersLineOne = 0;
                            int numberofNonWhiteSpaceCharactersLineThree = 0;
                            for (int counter = 0; counter < lineOneLength; counter++) {
                                if (lines[0].charAt(counter) != ' '){
                                    numberofNonWhiteSpaceCharactersLineOne++;
                                }
                            }

                            for (int counter = 0; counter < lineThreeLength; counter++) {
                                if (lines[2].charAt(counter) != ' '){
                                    numberofNonWhiteSpaceCharactersLineThree++;
                                }
                            }
                            //Check for first and second lines, if they are empty, then display a warning message:
                            if (numberofNonWhiteSpaceCharactersLineOne == 2 && numberofNonWhiteSpaceCharactersLineThree == 2){
                                holder.registerProblem(elem, SampleBundle.message("comments.fix.tooltip.blockcomments"),
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new BlockCommentsQuickFix(elem));
                            }
                        }
                        //If there are more than 3 lines in the block comment, then:
                        else if (lines.length > 3){
                            int validCommentLines = 0;
                            int numberofLines = lines.length;
                            int eachlineLength;
                            for (int count = 0; count< numberofLines; count++){
                                eachlineLength = lines[count].length();
                                for (int counter = 0; counter < eachlineLength; counter++){
                                    if (!Character.isWhitespace(lines[count].charAt(counter))){
                                        validCommentLines++;
                                        counter = eachlineLength;
                                    }
                                }
                            }
                            //Check first line of the block comment for more characters than just "/*" and whitespaces
                            int firstLineNumberofNonWhitespaceCharacters = 0;
                            for (int counting = 0; counting < lines[0].length(); counting++) {
                                if (!Character.isWhitespace(lines[0].charAt(counting))){
                                    firstLineNumberofNonWhitespaceCharacters++;
                                }
                            }

                            //Check last line of the block comment for more characters than just "*/" and whitespaces
                            int secondLineNumberofNonWhitespaceCharacters = 0;
                            for (int counting = 0; counting < lines[numberofLines - 1].length(); counting++) {
                                if (!Character.isWhitespace(lines[numberofLines - 1].charAt(counting))){
                                    secondLineNumberofNonWhitespaceCharacters++;
                                }
                            }

                            //If less than four of all lines in the entire block comment are not empty, then display a warning
                            if (validCommentLines< 4){
                                //If either first or last or both lines are empty with only the commenting symbols, then throw a warning
                                if (firstLineNumberofNonWhitespaceCharacters <= 2 && secondLineNumberofNonWhitespaceCharacters <= 2) {
                                    holder.registerProblem(elem, SampleBundle.message("comments.fix.tooltip.blockcomments"),
                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new BlockCommentsQuickFix(elem));
                                }
                            }
                        }
                        //If there is only one line in the entire block comment, then:
                        else if (lines.length == 1) {
                            int giveWarning = 0;
                            PsiElement nextOne = token.getParent().getFirstChild();
                            while (!(nextOne instanceof PsiComment)) {
                                if (nextOne.getText().equals("(")) {
                                    giveWarning++;
                                }
                                nextOne = nextOne.getNextSibling();
                            }
                            //If the block comment is not within a statement, then display a warning message:
                            if (giveWarning == 0) {
                                holder.registerProblem(elem, SampleBundle.message("comments.fix.tooltip.blockcomments"),
                                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new BlockCommentsQuickFix(elem));
                            }
                        }
                    }
                }
                super.visitElement(elem);
            }
        };
    }
}
