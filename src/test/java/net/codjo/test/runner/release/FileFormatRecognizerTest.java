/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.test.runner.release;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FileFormatRecognizer}.
 */
public class FileFormatRecognizerTest extends TestCase {
    private FileFormatRecognizer recognizer;
    private File tempBadDirectory;


    public void test_isReleaseTestFileFormat_nok()
          throws Exception {
        assertFalse("Un fichier introuvable n'est pas un ReleaseTest",
                    recognizer.isReleaseTestFileFormat("c:/unknown.file"));

        assertFalse("Un repertoire n'est pas un ReleaseTest",
                    recognizer.isReleaseTestFileFormat(getDirectory()));

        assertFalse("Un contenu non ReleaseTest",
                    recognizer.isReleaseTestFileFormat(new StringReader("not release test")));
    }


    public void test_isReleaseTestFile_ok() throws Exception {
        assertTrue("Un repertoire vide n'est pas un répertoire de ReleaseTest",
                   recognizer.isReleaseTestFile(getDirectory()));
        assertTrue("Un fichier au format ReleaseTest",
                   recognizer.isReleaseTestFile(toPath("ReleaseTestFile.xml")));
    }


    public void test_isReleaseTestFile_nok() throws Exception {
        assertFalse("Un fichier introuvable n'est pas un ReleaseTest",
                    recognizer.isReleaseTestFile("c:/unknown.file"));

        assertFalse("Un repertoire vide n'est pas un répertoire de ReleaseTest",
                    recognizer.isReleaseTestFile(tempBadDirectory.getAbsolutePath()));
    }


    public void test_isReleaseTestFileFormat_ok()
          throws Exception {
        assertTrue("Un contenu ReleaseTest",
                   recognizer.isReleaseTestFileFormat(
                         new StringReader("...<release-test name='MyTest' ...")));

        assertTrue("Un fichier au format ReleaseTest",
                   recognizer.isReleaseTestFileFormat(toPath("ReleaseTestFile.xml")));
    }


    @Override
    protected void setUp() throws Exception {
        recognizer = new FileFormatRecognizer();
        tempBadDirectory = new File(System.getProperty("java.io.tmpdir"), "tempBadDirectory");
        tempBadDirectory.mkdirs();
        File.createTempFile("pasTest", ".xml", tempBadDirectory);
    }


    @Override
    protected void tearDown() throws Exception {
        new File(tempBadDirectory.getAbsolutePath(), "pasTest.xml").delete();
        tempBadDirectory.delete();
    }


    private String toPath(String name) {
        URL resource = getClass().getResource(name);
        String path = resource.getFile();
        return (path.startsWith("/") ? path.substring(1) : path);
    }


    private String getDirectory() {
        return new File(toPath("ReleaseTestFile.xml")).getParent();
    }
}
