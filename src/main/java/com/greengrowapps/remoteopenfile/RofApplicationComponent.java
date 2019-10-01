package com.greengrowapps.remoteopenfile;

import com.greengrowapps.remoteopenfile.services.RofAppService;
import com.greengrowapps.remoteopenfile.settings.RofSettings;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

public class RofApplicationComponent implements BaseComponent {

    public void initComponent() {
        System.out.println("MyApplicationComponent initComponent");
        RofAppService service = ServiceManager.getService(RofAppService.class);
        if(RofSettings.isRunOnStartup()) {
            service.start();
        }
    }

    public void disposeComponent() {
        System.out.println("MyApplicationComponent disposeComponent");
        RofAppService service = ServiceManager.getService(RofAppService.class);
        service.stop();
    }

    @NotNull
    public String getComponentName() {
        System.out.println("Get component name");
        return "RofApplicationComponent";
    }
}
