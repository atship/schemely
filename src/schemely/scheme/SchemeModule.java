package schemely.scheme;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeIcons;

import javax.swing.*;

public class SchemeModule extends ModuleType {
    public SchemeModule() {
        super("Scheme");
    }

    @NotNull
    @Override
    public ModuleBuilder createModuleBuilder() {
        return new SchemeModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Scheme";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Scheme Project";
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return SchemeIcons.SCHEME_MODULE_ICON;
    }
}
