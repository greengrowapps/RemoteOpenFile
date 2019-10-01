package com.greengrowapps.remoteopenfile.settings;

import com.intellij.ide.util.PropertiesComponent;
import gherkin.lexer.Ro;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RofSettingsGui {
    private JPanel content;
    private JCheckBox runOnStartupCheck;

    public RofSettingsGui() {
        PropertiesComponent settings = PropertiesComponent.getInstance();
        runOnStartupCheck.setSelected(RofSettings.isRunOnStartup());

        runOnStartupCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                RofSettings.setRunOnStartup(runOnStartupCheck.isSelected());
            }
        });
    }

    public JPanel getContent() {
        return content;
    }
}
