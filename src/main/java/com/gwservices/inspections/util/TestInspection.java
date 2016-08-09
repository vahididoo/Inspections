package com.gwservices.inspections.util;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/3/2016.
 */
public class TestInspection extends BaseLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
  /*              Project project = holder.getProject();
                ProjectFileIndex fileIndex = ProjectFileIndex.SERVICE.getInstance(project);
                System.out.println(project.getBasePath());
                VirtualFile base = VirtualFileManager.getInstance().findFileByUrl("file://" + project.getBasePath() +
                        "/modules/base.zip");*/
                System.out.println(file.getModificationStamp() + "\n");

/*
                try {
                    ZipFile theZipFile = new ZipFile(base.getCanonicalPath());
                    Enumeration<? extends ZipEntry> entries = theZipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry zipEntry = entries.nextElement();
                        System.out.println(zipEntry.getName()+"\t"+zipEntry.getSize());
                        base.getModificationStamp()
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
            }
        };
    }
}
