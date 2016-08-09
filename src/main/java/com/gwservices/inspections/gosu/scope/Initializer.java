package com.gwservices.inspections.gosu.scope;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

/**
 * Created by vmansoori on 8/6/2016.
 */
public class Initializer implements ProjectComponent {
    public Initializer(Project project) {
        GWProductManager.getInstance(project);
    }

    @Override
    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    @Override
    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Initializer";
    }

    @Override
    public void projectOpened() {
        // called when project is opened
    }

    @Override
    public void projectClosed() {
        // called when project is being closed
    }
}
