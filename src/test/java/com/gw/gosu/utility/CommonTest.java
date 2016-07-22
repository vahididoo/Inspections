package com.gw.gosu.utility;

import gw.plugin.ij.lang.psi.impl.statements.GosuIfStatementImpl;
import junit.framework.TestCase;

public class CommonTest extends TestCase {

    public void testSearchForElementOfTypeReturnNullForNullInput() {
        assertNull(Common.searchForElementOfType(null, GosuIfStatementImpl.class));
    }
}
