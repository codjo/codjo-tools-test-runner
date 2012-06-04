package net.codjo.test.runner.release;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfiguration;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import java.io.File;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReleaseTestRunConfiguration extends RuntimeConfiguration {
    private static final String FILE_ATTRIBUTE = "file";
    private static final String MODULE_ATTRIBUTE = "module";
    private static final String VM_PARAMETERS_ATTRIBUTE = "vmParameters";
    private final FileFormatRecognizer recognizer = new FileFormatRecognizer();
    private String releaseTestFileName;
    private String vmParameters;
    private String[] vmParameterList;
    private Module targetModule;


    public ReleaseTestRunConfiguration(ReleaseTestConfigurationFactory factory,
                                       Project project,
                                       String name) {
        super(name, project, factory);
    }


    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        String moduleName = JDOMExternalizer.readString(element, MODULE_ATTRIBUTE);
        if (moduleName == null) {
            return;
        }

        String vmParametersAttribute = JDOMExternalizer.readString(element, VM_PARAMETERS_ATTRIBUTE);
        setVMParameters(vmParametersAttribute);

        String fileAttribute = JDOMExternalizer.readString(element, FILE_ATTRIBUTE);
        setReleaseTestFileName(fileAttribute);

        setTargetModule(ModuleManager.getInstance(getProject()).findModuleByName(moduleName));
    }


    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        JDOMExternalizer.write(element, FILE_ATTRIBUTE, getReleaseTestFileName());
        JDOMExternalizer.write(element, VM_PARAMETERS_ATTRIBUTE, getVMParameters());
        JDOMExternalizer.write(element, MODULE_ATTRIBUTE, getTargetModuleName());
    }


    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ReleaseTestRunConfigurationEditor(getProject());
    }


    public RunProfileState getState(@NotNull Executor executor,
                                    @NotNull ExecutionEnvironment executionEnvironment)
          throws ExecutionException {
        ReleaseTestRunProfileState commandLineState = new ReleaseTestRunProfileState(executionEnvironment);
        TextConsoleBuilder consoleBuilder =
              TextConsoleBuilderFactory.getInstance().createBuilder(getProject());
//        Migration IDEA 10.5
//        consoleBuilder.addFilter(
//              new RegexpFilter(getProject(),
//                               "\\("
//                               + RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS
//                               + "\\)"));
        commandLineState.setConsoleBuilder(consoleBuilder);
        return commandLineState;
    }


    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (targetModule == null) {
            throw new RuntimeConfigurationException("Bad target module", "Bad Module");
        }
        if (releaseTestFileName == null
            || "".equals(releaseTestFileName)
            || !new File(releaseTestFileName).exists()) {
            throw new RuntimeConfigurationException("ReleaseTest file is invalid", "Bad File");
        }
        if (!recognizer.isReleaseTestFile(releaseTestFileName)) {
            throw new RuntimeConfigurationException("ReleaseTest file format is invalid", "Bad File Format");
        }
    }


    public String getReleaseTestFileName() {
        return releaseTestFileName;
    }


    public void setReleaseTestFileName(String releaseTestFileName) {
        this.releaseTestFileName = releaseTestFileName;
    }


    public Module getTargetModule() {
        return targetModule;
    }


    public void setTargetModule(Module targetModule) {
        this.targetModule = targetModule;
    }


    private String getTargetModuleName() {
        return (getTargetModule() != null ? getTargetModule().getName() : null);
    }


    public String getVMParameters() {
        return vmParameters;
    }


    public void setVMParameters(@Nullable String vmParameters) {
        this.vmParameters = vmParameters;
        if (vmParameters != null) {
            vmParameterList = ParametersList.parse(vmParameters);
        }
        else {
            vmParameterList = null;
        }
    }


    public String[] getVmParameterAsArray() {
        return vmParameterList;
    }
}
