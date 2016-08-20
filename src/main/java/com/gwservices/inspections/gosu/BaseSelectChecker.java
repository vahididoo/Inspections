package com.gwservices.inspections.gosu;

import com.gwservices.inspections.gosu.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;

/**
 * Created by vmansoori on 7/17/2016.
 */
public abstract class BaseSelectChecker extends BaseCallerCalleeChecker {

    public static final String GW_API_QUERY_IQUERY_BEAN_RESULT = "gw.api.database.IQueryBeanResult<T>";

    @Override
    protected boolean isCallerOfInterestedType(PsiType returnType) {
        return InheritanceUtil.isInheritor(returnType, "gw.api.database.IQueryBeanResult");
    }

    @Override
    protected boolean isCallerOfInterestedName(String callerName) {
        return isMatch(callerName, "select");
    }
}
