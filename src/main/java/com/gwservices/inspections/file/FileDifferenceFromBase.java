package com.gwservices.inspections.file;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.ide.projectView.impl.ModuleUrl;
import com.intellij.openapi.diff.impl.external.ArchiveDiffTool;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/19/2016.
 */
public class FileDifferenceFromBase extends LocalInspectionTool{

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, final boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                if(isOnTheFly){
                    return;
                }

                Module configuration = ModuleManager.getInstance(file.getProject()).findModuleByName("configuration");

            }
        };
    }
}
