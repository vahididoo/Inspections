package com.gwservices.inspections.gosu.collections;

/**
 * Created by vmansoori on 7/31/2016.
 */
public class CheckCollectionWhereFirst extends BaseCollectionChecker {

    @Override
    protected String getCalled() {
        return "first";
    }

    @Override
    protected String getCaller() {
        return "Where";
    }

    @Override
    protected String getSuggestion() {
        return "firstWhere";
    }
}
