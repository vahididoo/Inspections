package com.gwservices.inspections.entity;

import com.guidewire.commons.entity.type2.CarbonEntityTypeLoader;
import com.guidewire.commons.metadata.schema.dm2.EntityNode;
import com.guidewire.commons.metadata.schema.dm2.EntityNodeBuilder;
import com.guidewire.util.gosu.GosuModuleUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import gw.entity.IEntityType;
import gw.internal.ext.org.apache.commons.collections.IteratorUtils;
import gw.lang.reflect.IType;
import gw.lang.reflect.TypeSystem;
import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/17/2016.
 */
public class CheckEntity extends LocalInspectionTool {


    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitFile(PsiFile file) {
                if (file.getVirtualFile().getExtension().equalsIgnoreCase("etx") ||
                        file.getVirtualFile().getExtension().equalsIgnoreCase("eti")) {
                    IType type = TypeSystem.getByFullName("entity" + file.getVirtualFile().getNameWithoutExtension());
                    holder.registerProblem(file, "Entity name " + type.getName());


                }
            }
        };
    }
}
