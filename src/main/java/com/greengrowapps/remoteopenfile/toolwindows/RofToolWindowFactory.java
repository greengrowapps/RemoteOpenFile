package com.greengrowapps.remoteopenfile.toolwindows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey.Chursin
 * Date: Aug 25, 2010
 * Time: 2:09:00 PM
 */
public class RofToolWindowFactory implements ToolWindowFactory {

  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

    StautsWindow statusWindow = new StautsWindow(toolWindow);
    Content statusWindowContent = contentFactory.createContent(statusWindow.getContent(), "status", false);
    statusWindowContent.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
    statusWindowContent.setIcon(AllIcons.Ide.LocalScope);
    toolWindow.getContentManager().addContent(statusWindowContent);


    LogWindow logWindow = new LogWindow();
    Content logWindowContent = contentFactory.createContent(logWindow.getContent(), "Log", false);
    logWindowContent.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
    logWindowContent.setIcon(AllIcons.Debugger.Console);
    toolWindow.getContentManager().addContent(logWindowContent);
  }
}
