package com.gwservices.inspections.pcf;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.xml.XmlText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.ParseException;

/**
 * Created by vmansoori on 7/26/2016.
 */
public class CheckTooMuchCodeInPCF extends LocalInspectionTool {

    private int threshold = 5;

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {

        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);

                if (element.getText().equalsIgnoreCase("code")) {
                    for (PsiElement psiElement : element.getParent().getChildren()) {
                        if (psiElement instanceof XmlText) {
                            String[] strings = psiElement.getText().split("\r\n|\r|\n");
                            if (strings != null && strings.length > threshold) {
                                holder.registerProblem(element.getParent(), "Number of lines of code in PCF " + strings.length + " exceeds " + threshold);
                            }
                        }
                    }
                }
            }
        };
    }


    @Nullable
    @Override
    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Acceptable threshodl: ");
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(threshold, 1, 50, 1));
        panel.add(label);
        panel.add(spinner);
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    spinner.commitEdit();
                } catch (ParseException e1) {

                }
                threshold = (int) spinner.getModel().getValue();
            }
        });
        return panel;
    }
}
