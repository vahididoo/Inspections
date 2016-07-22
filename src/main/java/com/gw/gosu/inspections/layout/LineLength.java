
package com.gw.gosu.inspections.layout;

import com.gw.gosu.inspections.SampleBundle;
import com.gw.gosu.utility.Common;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.codeInspection.ex.ProblemDescriptorImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiDocumentManagerImpl;
import gw.plugin.ij.lang.psi.IGosuFileBase;
import gw.plugin.ij.lang.psi.api.statements.IGosuField;
import gw.plugin.ij.lang.psi.impl.GosuClassFileImpl;
import gw.plugin.ij.lang.psi.impl.GosuElementVisitor;
import gw.plugin.ij.lang.psi.impl.GosuProgramFileImpl;
import gw.plugin.ij.lang.psi.impl.statements.typedef.GosuClassDefinitionImpl;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This inspection checks if a condition exceeds a certain number of characters.
 */
public class LineLength extends BaseLocalInspectionTool implements FileCheckingInspection {

    @Nls
    @NotNull
    @Override
    public final String getGroupDisplayName() {
        return SampleBundle.message("layout.group.name");
    }

    @Nls
    @NotNull
    @Override
    public final String getDisplayName() {
        return SampleBundle.message("layout.inspection.linetoolong");
    }

    @NotNull
    @Override
    public final String getShortName() {
        return "LineLength";
    }

    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        return super.checkFile(file, manager, isOnTheFly);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new GosuElementVisitor() {
            @Override
            public void visitClassFile(GosuClassFileImpl classFile) {
                final PsiFile file = classFile.getContainingFile();
                Document document = PsiDocumentManagerImpl.getInstance(classFile.getProject())
                        .getDocument(file);
                int maxLength = Common.getMaxLineLength(classFile.getProject());
                for (int i = 0, ii = document.getLineCount(); i < ii; i++) {
                    int lineStart = document.getLineStartOffset(i);
                    int lineEnd = document.getLineEndOffset(i);
                    int lineLength = lineEnd - lineStart;
                    if (lineLength > maxLength) {
                        //Select the range in which we want to highlight
                        PsiElement lineStartElement = file.findElementAt(lineStart);
                        if (lineStartElement instanceof PsiPlainText) continue;
                        PsiElement lineEndElement = file.findElementAt(lineEnd);

                        if (lineStartElement instanceof PsiWhiteSpace) {
                            lineStartElement = lineStartElement.getNextSibling();
                        }
                        if (lineEndElement instanceof PsiWhiteSpace) {
                            lineEndElement = lineEndElement.getPrevSibling();
                        }

                        //Create the descriptor for the problem
                        ProblemDescriptor descriptor = new ProblemDescriptorImpl(
                                lineStartElement, lineEndElement,
                                SampleBundle.message("layout.name.linetoolong", lineLength, maxLength),
                                null, ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                false, /*range*/ null, isOnTheFly);

                        //Register the warning
                        holder.registerProblem(descriptor);
                    }
                }
            }
        };
    }
}
