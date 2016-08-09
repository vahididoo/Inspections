package com.gwservices.inspections.gosu.scope;

import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.scope.packageSet.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/3/2016.
 */
public class ClientScope extends NamedScope {
    private static final String NAME = "Client Modified";

    public ClientScope() {
        super(NAME, new AbstractPackageSet("ClientModified") {

            @Override
            public boolean contains(VirtualFile file, NamedScopesHolder holder) {
                return contains(file, holder.getProject(), holder);
            }

            @Override
            public boolean contains(VirtualFile file, @NotNull Project project, @Nullable NamedScopesHolder holder) {
                final ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
                return file != null && index.isInSource(file) && !index.isInTestSourceContent(file) && !index
                        .isInLibraryClasses(file) && !index.isInLibrarySource(file) && GWProductManager.getInstance
                        (holder.getProject()).isClientFile(file);

            }
        });

    }

}
