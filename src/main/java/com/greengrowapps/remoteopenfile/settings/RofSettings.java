package com.greengrowapps.remoteopenfile.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RofSettings implements SearchableConfigurable {
    private static final String RUN_ON_STARTUP = "ROF_SETTINGS_RUN_ON_STARTUP";

    public static boolean isRunOnStartup(){
        PropertiesComponent settings = PropertiesComponent.getInstance();
        return settings.getBoolean(RUN_ON_STARTUP,false);
    }
    public static void setRunOnStartup(boolean run){
        PropertiesComponent settings = PropertiesComponent.getInstance();
        settings.setValue(RUN_ON_STARTUP,run);
    }

    @NotNull
    @Override
    public String getId() {
        return "preferences.RemoteOpenFileSettings";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Rof settings display name";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return new RofSettingsGui().getContent();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
