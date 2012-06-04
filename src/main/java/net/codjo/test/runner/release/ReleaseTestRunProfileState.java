package net.codjo.test.runner.release;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathsList;

public class ReleaseTestRunProfileState extends JavaCommandLineState {
    private static final String RUNNER_CLASS = "net.codjo.test.release.ReleaseTestRunner";
    private static final String RUNNER_CLASS_LEGACY = "com.agf.test.release.ReleaseTestRunner";


    protected ReleaseTestRunProfileState(ExecutionEnvironment executionEnvironment) {
        super(executionEnvironment);
    }


    @Override
    protected JavaParameters createJavaParameters() throws ExecutionException {
        JavaParameters javaParameters = new JavaParameters();

        javaParameters.setJdk(determineProjectJDK());
        javaParameters.configureByModule(getConfig().getTargetModule(), JavaParameters.JDK_AND_CLASSES_AND_TESTS);

        PathsList classPath = javaParameters.getClassPath();
        boolean isNewFramework = classPath.getPathsString().contains("codjo");
        javaParameters.setMainClass(isNewFramework ? RUNNER_CLASS : RUNNER_CLASS_LEGACY);

        javaParameters.setWorkingDirectory(determineModuleDirectory());

        String userHome = System.getProperty("user.home");
        javaParameters.getVMParametersList()
              .add("-Dlog4j.configuration=file:///" + userHome + "/log4j.properties");

        if (getConfig().getVMParameters() != null && !"".equals(getConfig().getVMParameters().trim())) {
            javaParameters.getVMParametersList().addAll(getConfig().getVmParameterAsArray());
        }

        javaParameters.getProgramParametersList().add(getConfig().getReleaseTestFileName());

        return javaParameters;
    }


    private String determineModuleDirectory() {
        //noinspection ConstantConditions
        return getConfig().getTargetModule().getModuleFile().getParent().getPath();
    }


    private Sdk determineProjectJDK() {
        return ProjectRootManager.getInstance(getConfig().getProject()).getProjectJdk();
    }


    private ReleaseTestRunConfiguration getConfig() {
        return (ReleaseTestRunConfiguration)getRunnerSettings().getRunProfile();
    }
}
