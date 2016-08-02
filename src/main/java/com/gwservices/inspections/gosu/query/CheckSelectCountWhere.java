package com.gwservices.inspections.gosu.query;

import org.jetbrains.annotations.NotNull;

/**
 * Created by vmansoori on 7/30/2016.
 */
public class CheckSelectCountWhere extends BaseSelectChecker {
    @Override
    @NotNull
    protected String getInterestingKeyword() {
        return "countwhere";
    }
}
