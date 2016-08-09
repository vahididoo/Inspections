package com.gwservices.inspections.util;

import com.intellij.psi.PsiType;
import com.intellij.psi.util.InheritanceUtil;

/**
 * Created by vmansoori on 8/2/2016.
 */
public class CommonUtil {

    public static boolean isCollection(PsiType type){
        return InheritanceUtil.isInheritor(type, "java.util.Collection");
    }

    public static boolean isQueryResult(PsiType type){
        return InheritanceUtil.isInheritor(type, "gw.api.database.IQueryBeanResult<T>");
    }


}
