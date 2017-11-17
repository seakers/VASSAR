package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import rbsa.eoss.*;
import rbsa.eoss.local.Params;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

class EvalPerformanceTest {

    private Params params;
    private ArchitectureEvaluator AE = null;
    private ArchitectureGenerator AG = null;

    public void initJess() {
        // Set a path to the project folder
        String path = System.getProperty("user.dir");

        // Initialization
        String search_clps = "";
        params = Params.initInstance(path, "FUZZY-ATTRIBUTES", "test","normal", search_clps);//FUZZY or CRISP
        params.inUnitTest = true;
        AE = ArchitectureEvaluator.getInstance();
        AG = ArchitectureGenerator.getInstance();
        AE.init(1);
    }


    @Test
    void evalPerformance() {
        initJess();

        String bitString = "100000110001010000100010000001000000000011000001000000011110";
        double expectedCost = 6164.717218488444;
        double expectedScience = 0.2596902897905202;
        String expectedRules = "";
        try {
            expectedRules = new String(Files.readAllBytes(Paths.get("java/src/test/correct_rules.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        // Generate a new architecture
        Architecture architecture = AG.defineNewArch(bitString);

        // Evaluate the architecture
        final ByteArrayOutputStream jessCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(jessCapture));
        Result result = AE.evaluateArchitecture(architecture,"Slow");
        final String rules = jessCapture.toString();

        // Save the score and the cost
        double cost = result.getCost();
        double science = result.getScience();

        assertEquals(expectedCost, cost);
        assertEquals(expectedScience, science);
        assertEquals(expectedRules, rules);
    }
}