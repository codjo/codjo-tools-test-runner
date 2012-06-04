package net.codjo.test.runner.release;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import junit.framework.TestCase;
/**
 *
 */
public class ReleaseTestConfigurationTypeTest extends TestCase {

    public void test_getVMParametersForReleaseTest() throws Exception {
        String releaseConfigContents
              = "bla=1.2\n "
                + "vmParameters=-Djava.library.path=\"${basedir}/sam-gui/target/dll\" "
                + "-DotherParam=toto";

        String vmParameterString = ReleaseTestRunConfigurationType
              .getVMParametersForReleaseTest(new ByteArrayInputStream(releaseConfigContents.getBytes()));

        assertEquals("-Djava.library.path=\"${basedir}/sam-gui/target/dll\" -DotherParam=toto",
                     vmParameterString);
    }


    public void test_getVMParametersForReleaseTest_twice() throws Exception {
        String releaseConfigContents
              = "bla=1.2\n "
                + "vmParameters=-Djava.library.path=\"${basedir}/sam-gui/target/dll\" \n "
                + "vmParameter=-DotherParam=toto";

        try {
            ReleaseTestRunConfigurationType
                  .getVMParametersForReleaseTest(new ByteArrayInputStream(releaseConfigContents.getBytes()));
            fail("Fichier de config incorrect");
        }
        catch (RuntimeException exception) {
            assertEquals(
                  "Fichier de config incorrect, propriétés incompatibles : vmParameters et vmParameter.",
                  exception.getMessage());
        }
    }


    public void test_getPropertyFromInputStream() throws Exception {
        Properties property = ReleaseTestRunConfigurationType
              .getPropertyFromInputStream(getClass().getResourceAsStream("release-test.config"));

        assertEquals("-Djava.library.path=\"${basedir}/sam-gui/target/dll\"",
                     property.getProperty("vmParameter"));
    }
}
