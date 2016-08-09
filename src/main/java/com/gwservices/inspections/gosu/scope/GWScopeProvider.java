package com.gwservices.inspections.gosu.scope;

import com.intellij.psi.search.scope.packageSet.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Created by vmansoori on 8/3/2016.
 */
public class GWScopeProvider implements CustomScopesProvider {
    private final ClientScope clientScope = new ClientScope();

    @NotNull
    @Override
    public List<NamedScope> getCustomScopes() {
        return Collections.singletonList(clientScope);
    }
}
