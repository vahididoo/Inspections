package com.gwservices.inspections.metrics.method;

import com.intellij.codeInspection.ex.*;
import com.intellij.codeInspection.ui.*;

import javax.swing.*;
import java.util.*;

/**
 * Created by vmansoori on 8/17/2016.
 */
public class BaseParametrizedInspectionTool extends BaseLocalInspectionTool {

    protected final JPanel getSingleCheckboxOptionsPanel(String label, String property) {
        return new SingleCheckboxOptionsPanel(label, this, property);
    }

    protected final JPanel getSingleIntegerOptionsPanel(String label, String property) {
        return new SingleIntegerFieldOptionsPanel(label, this, property);
    }

    protected final MultipleCheckboxOptionsPanel getMultipleCheckboxOptionsPanel() {
        return new MultipleCheckboxOptionsPanel(this);
    }

    protected final JPanel getListEditFormOptionsPanel(String label, List<String> entries) {
        return (JPanel) new ListEditForm(label, entries).getContentPanel();
    }

}
