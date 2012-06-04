package net.codjo.test.runner.release;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 */
public class ReleaseTestRunConfigurationTest {
    @Test
    public void testTokenParameters() throws Exception {
        ReleaseTestRunConfiguration config = new ReleaseTestRunConfiguration(mockedValue(), null, "nn");
        config.setVMParameters("arg1 arg2 arg3");
        Assert.assertArrayEquals("", new String[]{"arg1", "arg2", "arg3"}, config.getVmParameterAsArray());
    }


    @Test
    public void testTokenParametersNull() throws Exception {
        ReleaseTestRunConfiguration config = new ReleaseTestRunConfiguration(mockedValue(), null, "nn");
        config.setVMParameters(null);
        Assert.assertNull(config.getVmParameterAsArray());
    }


    private static ReleaseTestConfigurationFactory mockedValue() {
        return new ReleaseTestConfigurationFactory(new ReleaseTestRunConfigurationType());
    }
}
