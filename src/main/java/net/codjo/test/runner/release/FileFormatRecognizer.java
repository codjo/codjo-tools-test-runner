/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.test.runner.release;
import com.intellij.openapi.diagnostic.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
/**
 * Classe utilitaire permettant de reconnaître un fichier au format release-test.
 */
class FileFormatRecognizer {
    private Logger logger = Logger.getInstance("idea.jalopy.JalopyPlugin");


    FileFormatRecognizer() {
    }


    boolean isReleaseTestFileFormat(Reader reader)
          throws IOException {
        // Algorithme extrêmement complexe est évolué permettant de déterminer le format :)
        char[] buffer = new char[10000];
        reader.read(buffer);
        String content = new String(buffer);
        return content.contains("<release-test");
    }


    public boolean isReleaseTestFile(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File currentFile : files) {
                if (currentFile.isFile() && isReleaseTestFileFormat(currentFile.getAbsolutePath())) {
                    return true;
                }
            }
        }
        else {
            return isReleaseTestFileFormat(filePath);
        }

        return false;
    }


    public boolean isReleaseTestFileFormat(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                return false;
            }
            FileReader reader = new FileReader(file);
            try {
                return isReleaseTestFileFormat(reader);
            }
            finally {
                reader.close();
            }
        }
        catch (IOException e) {
            logger.error("Impossible de déterminer si le fichier est au format ReleaseTest",
                         e);
            return false;
        }
    }
}
