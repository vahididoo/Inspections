package com.gwservices.inspections.gosu.collections;

import com.gwservices.inspections.metrics.method.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

/**
 * Created by vmansoori on 8/20/2016.
 */
public class TestListEditForm extends BaseParametrizedInspectionTool {
    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        List<String> callerModel = Arrays.asList("where", "whereTypeIs");
        JPanel callerPanel = getListEditFormOptionsPanel("Callers", callerModel);
        List<String> calledModel = Arrays.asList("where", "whereTypeIs");
        JPanel called = getListEditFormOptionsPanel("Called", calledModel);

        return callerPanel;
    }
}
