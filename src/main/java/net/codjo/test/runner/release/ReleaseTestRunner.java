/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.test.runner.release;
/**
 * Classe simulant le veritable ReleaseTestRunner. La classe ReleaseTestRunner se trouve dans la librairie :
 *
 * <p> <a href="http://a7wj111:8080/codjo-lib/docs/codjo-test-release">codjo-test</a> </p>
 *
 * @version $Revision: 1.1 $
 * @see ReleaseTestRunProfileState#RUNNER_CLASS
 */
public class ReleaseTestRunner {
    private ReleaseTestRunner() {
    }


    public static void main(String[] args) throws Exception {
        System.out.println("Simulation de l'exécution d'un test release.");
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] = " + args[i]);
        }

        try {
            throw new IllegalStateException("Bobo");
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
