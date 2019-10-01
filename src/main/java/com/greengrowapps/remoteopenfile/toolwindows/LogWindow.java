package com.greengrowapps.remoteopenfile.toolwindows;

import com.greengrowapps.remoteopenfile.services.LogService;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

public class LogWindow implements LogService.ROFLogger {
    private JTextArea consoleText;
    private JPanel content;

    public LogWindow(){
        consoleText.setText("");
        writeLine("Start");
        LogService service = ServiceManager.getService(LogService.class);
        service.registerLogger(this);
    }

    public JComponent getContent() {
        return content;
    }

    @Override
    public void writeLine(String line) {
        consoleText.setText(consoleText.getText()+ line+"\n");
    }
}
