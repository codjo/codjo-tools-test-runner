package net.codjo.test.runner.release;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class ReleaseTestConfigurationFactory extends ConfigurationFactory {
    public ReleaseTestConfigurationFactory(ReleaseTestRunConfigurationType releaseTestRunConfigurationType) {
        super(releaseTestRunConfigurationType);
    }


    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new ReleaseTestRunConfiguration(this, project, "Une Config");
    }
}
