package com.gwservices.inspections.gosu.scope;

import com.intellij.ide.util.scopeChooser.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/3/2016.
 */
public class GWScopeDescriptorProvider implements ScopeDescriptorProvider {
    @NotNull
    @Override
    public ScopeDescriptor[] getScopeDescriptors(Project project) {
        return new ScopeDescriptor[0];
    }
}
