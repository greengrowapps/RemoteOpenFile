package com.greengrowapps.remoteopenfile;

import com.greengrowapps.remoteopenfile.server.RemoteOpenFileServerServer;
import com.greengrowapps.remoteopenfile.services.LogService;
import com.greengrowapps.remoteopenfile.services.RofAppService;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class HelloAction extends AnAction  {
    private static RemoteOpenFileServerServer server = null;
    Project project=null;

    public HelloAction() {
        super("Hello");
    }

    public void actionPerformed(AnActionEvent event) {
        int lineNumber = 10;
        String filename = "asdfasdf.txxt";

        this.project= event.getProject();

        RofAppService service = ServiceManager.getService(RofAppService.class);
        service.start();

        service.openFileAndLineNumber(this.project,filename,lineNumber);

        LogService logger = ServiceManager.getService(LogService.class);
logger.writeLine("Action called");


    }


}