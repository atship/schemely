package schemely.actions;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.compound.CompoundRunConfigurationSettingsEditor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.SchemeIcons;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

public class SchemeRun extends ConfigurationTypeBase {
    protected SchemeRun() {
        super("Scheme", "Scheme", "Scheme", SchemeIcons.SCHEME_ICON);

        addFactory(new ConfigurationFactory(this) {

            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull final Project project) {
                return new ModuleBasedConfiguration<RunConfigurationModule>(new RunConfigurationModule(project), this) {

                    @NotNull
                    @Override
                    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
                        return new CompoundRunConfigurationSettingsEditor(project);
                    }

                    @Nullable
                    @Override
                    public GlobalSearchScope getSearchScope() {
                        return GlobalSearchScope.projectScope(project);
                    }

                    @Nullable
                    @Override
                    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment environment) throws ExecutionException {
                        return new CommandLineState(environment) {
                            @NotNull
                            @Override
                            protected ProcessHandler startProcess() throws ExecutionException {
                                VirtualFile virtualFile = FileEditorManager.getInstance(project).getSelectedFiles()[0];
                                Module module = ModuleUtilCore.findModuleForFile(virtualFile, project);
                                String url = virtualFile.getPresentableUrl().replace("\\", "/");
                                String workingDir = project.getBasePath() + "/" + module.getName() + "/src/";
                                url = url.replace(workingDir, "");

                                GeneralCommandLine generalCommandLine = new GeneralCommandLine("bash", "-c", "source ~/.bashrc; scheme --libdirs \\$CHEZSCHEMELIBDIRS --script " + url);
                                generalCommandLine.setCharset(Charset.forName("UTF-8"));
                                generalCommandLine.setWorkDirectory(workingDir);
                                return new ColoredProcessHandler(generalCommandLine);
                            }
                        };
                    }

                    @Override
                    public Collection<Module> getValidModules() {
                        return Arrays.asList(ModuleManager.getInstance(project).getModules());
                    }

                    @Override
                    public boolean isCompileBeforeLaunchAddedByDefault() {
                        return false;
                    }

                    @Override
                    public boolean excludeCompileBeforeLaunchOption() {
                        return true;
                    }
                };
            }
        });
    }
}
