package schemely.actions;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;

public class SchemeRunProducer extends RunConfigurationProducer<ModuleBasedConfiguration> {
    protected SchemeRunProducer() {
        super(new SchemeRun());
    }

    @Override
    protected boolean setupConfigurationFromContext(ModuleBasedConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        configuration.setName(context.getModule().getName());
        configuration.setModule(context.getModule());
        configuration.setBeforeRunTasks(new ArrayList<BeforeRunTask>());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(ModuleBasedConfiguration configuration, ConfigurationContext context) {
        Module module = configuration.getConfigurationModule().getModule();
        if (module == null) {
            String moduleName = configuration.getName();
            if (moduleName != null) {
                module = ModuleManager.getInstance(context.getProject()).findModuleByName(moduleName);
                configuration.setModule(module);
            }
        }
        return module == context.getModule();
    }
}
