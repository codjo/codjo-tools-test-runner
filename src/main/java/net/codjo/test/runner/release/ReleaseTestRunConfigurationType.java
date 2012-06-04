package net.codjo.test.runner.release;
import com.intellij.execution.LocatableConfigurationType;
import com.intellij.execution.Location;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public class ReleaseTestRunConfigurationType implements LocatableConfigurationType {
    private final FileFormatRecognizer recognizer = new FileFormatRecognizer();
    private final ReleaseTestConfigurationFactory factory;
    private Icon icon;


    public ReleaseTestRunConfigurationType() {
        factory = new ReleaseTestConfigurationFactory(this);
    }


    public String getDisplayName() {
        return "Release Test";
    }


    public String getConfigurationTypeDescription() {
        return "Configuration d'exécution d'un Test au format codjo-test-release";
    }


    public Icon getIcon() {
        if (icon == null) {
            icon = IconLoader.getIcon("icon-small.png", getClass());
        }
        return icon;
    }


    @NotNull
    public String getId() {
        return "ReleaseTestRunConfigurationType";
    }


    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{factory};
    }


    public RunnerAndConfigurationSettings createConfigurationByLocation(Location location) {
        VirtualFile currentFile;
        if (location.getOpenFileDescriptor() == null) {
            PsiDirectory psiDirectory = (PsiDirectory)location.getPsiElement();
            currentFile = psiDirectory.getVirtualFile();
        }
        else {
            currentFile = location.getOpenFileDescriptor().getFile();
        }
        if (!recognizer.isReleaseTestFile(currentFile.getPath())) {
            return null;
        }

        Project project = location.getProject();
        RunnerAndConfigurationSettings settings = buildSettings(RunManager.getInstance(project), currentFile);

        ReleaseTestRunConfiguration configuration = (ReleaseTestRunConfiguration)settings.getConfiguration();
        configuration.setReleaseTestFileName(currentFile.getPath());
        configuration.setTargetModule(ProjectRootManager.getInstance(project)
              .getFileIndex()
              .getModuleForFile(currentFile));

        VirtualFile path = manageMultiReleaseTestModules(currentFile, project);

        if (path == null) {
            path = project.getBaseDir()
                  .findFileByRelativePath(
                        project.getName() + "-release-test/target/config/test-release.config");
        }
        configuration.setVMParameters(getVMParametersForReleaseTest(path));
        configuration.setName(configuration.getName());

        return settings;
    }


    public boolean isConfigurationByLocation(RunConfiguration runConfiguration, Location location) {
        return false;
    }


    static String getVMParametersForReleaseTest(InputStream inputStream)
          throws IOException, RuntimeConfigurationException {
        Properties properties = getPropertyFromInputStream(inputStream);

        if (properties.containsKey("vmParameters") && properties.containsKey("vmParameter")) {
            throw new RuntimeException(
                  "Fichier de config incorrect, propriétés incompatibles : vmParameters et vmParameter.");
        }

        String vmParameters = properties.getProperty("vmParameters");
        if (vmParameters != null) {
            return vmParameters;
        }

        return properties.getProperty("vmParameter");
    }


    static Properties getPropertyFromInputStream(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }


    private String getVMParametersForReleaseTest(VirtualFile ideaConfig) {
        try {
            InputStream inputStream = ideaConfig.getInputStream();

            return getVMParametersForReleaseTest(inputStream);
        }

        catch (Exception exception) {
            return exception.getLocalizedMessage();
        }
    }


    @SuppressWarnings({"ConstantConditions"})
    private VirtualFile manageMultiReleaseTestModules(VirtualFile currentFile, Project project) {
        VirtualFile releaseTestModuleFile = null;
        VirtualFile parentFile = currentFile;
        while (releaseTestModuleFile == null && parentFile != null) {
            parentFile = parentFile.getParent();
            if (parentFile != null && parentFile.getPath().endsWith("-release-test")) {
                releaseTestModuleFile = parentFile;
            }
        }
        if (releaseTestModuleFile != null) {
            String releaseTestPath = releaseTestModuleFile.getPath();
            int beginLength = releaseTestModuleFile.getParent().getParent().getPath().length();

            String baseTestReleasePath = releaseTestPath.substring(beginLength + 1);

            return project.getBaseDir()
                  .findFileByRelativePath(baseTestReleasePath + "/target/config/test-release.config");
        }
        return null;
    }


    private RunnerAndConfigurationSettings buildSettings(RunManager runManager, VirtualFile currentFile) {
        return runManager.createRunConfiguration(buildSettingsName(currentFile), factory);
    }


    private String buildSettingsName(VirtualFile currentFile) {
        if (currentFile.isDirectory()) {
            return "Stories " + currentFile.getName();
        }
        return "Story " + currentFile.getNameWithoutExtension();
    }
}
