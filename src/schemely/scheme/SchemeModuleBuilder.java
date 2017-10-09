package schemely.scheme;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SchemeModuleBuilder extends ModuleBuilder implements ModuleBuilderListener {

    public SchemeModuleBuilder() {
        addListener(this);
    }

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {

    }

    @Override
    public ModuleType getModuleType() {
        return new SchemeModule();
    }

    @Override
    public void moduleCreated(@NotNull Module module) {
        final String name = module.getName();
        ModuleRootManager instance = ModuleRootManager.getInstance(module);
        try {
            VirtualFile parent = module.getProject().getBaseDir();
            final VirtualFile root;
            root = parent.findFileByRelativePath(name);
            ModifiableRootModel model = instance.getModifiableModel();
            model.addContentEntry(root);
            ContentEntry[] contentEntries = model.getContentEntries();
            VirtualFile src = root.createChildDirectory(this, "src");
            contentEntries[0].addSourceFolder(src, false);
            model.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
