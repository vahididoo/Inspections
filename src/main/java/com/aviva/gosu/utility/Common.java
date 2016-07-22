package com.aviva.gosu.utility;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wli on 7/2/2015.
 */
public class Common {
    public static final Set COLLECTIONSUPPORTWHERE = new HashSet<String>();
//    private static final Set interestedComparisonOperator = new HashSet<String>();

    static {
        COLLECTIONSUPPORTWHERE.add("java.util.Collection");
        COLLECTIONSUPPORTWHERE.add("java.util.Set");
        COLLECTIONSUPPORTWHERE.add("java.util.List");
        COLLECTIONSUPPORTWHERE.add("java.util.Queue");
        COLLECTIONSUPPORTWHERE.add("java.util.Deque");
        COLLECTIONSUPPORTWHERE.add("java.util.SortedSet");
        COLLECTIONSUPPORTWHERE.add("java.util.NavigableSet");
        COLLECTIONSUPPORTWHERE.add("java.util.concurrent.BlockingQueue");
        COLLECTIONSUPPORTWHERE.add("java.util.concurrent.BlockingDeque");

//        interestedComparisonOperator.add(">");
//        interestedComparisonOperator.add(">=");
//        interestedComparisonOperator.add("!=");
//        interestedComparisonOperator.add("==");
    }
}
