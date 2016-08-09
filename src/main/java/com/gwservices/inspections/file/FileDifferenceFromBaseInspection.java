package com.gwservices.inspections.file;

import com.gwservices.inspections.gosu.scope.*;
import com.intellij.codeInspection.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 7/19/2016.
 */
public class FileDifferenceFromBaseInspection extends GlobalSimpleInspectionTool {

    @Override
    public void checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, @NotNull ProblemsHolder
            problemsHolder, @NotNull GlobalInspectionContext globalContext, @NotNull ProblemDescriptionsProcessor
            problemDescriptionsProcessor) {

        if (file != null && file.getVirtualFile() != null) {
            VirtualFile virtualFile = file.getVirtualFile();
            long bloatingFactor = GWProductManager.getInstance(manager.getProject()).getBloatingFactor(virtualFile);
            if (bloatingFactor > 0.0) {
                ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(file, "BloatingFactor:" +
                        bloatingFactor, (LocalQuickFix[]) null, ProblemHighlightType.WEAK_WARNING, false, false);
                problemDescriptionsProcessor.addProblemElement(globalContext.getRefManager().getReference(file),
                        problemDescriptor);
            }
        }
    }
}
