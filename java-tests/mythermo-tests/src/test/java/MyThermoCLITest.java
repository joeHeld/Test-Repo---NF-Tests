import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class MyThermoCLITest {

    /**
     * Helper: runs "python my_thermo.py <args>" from the repo root
     * and returns all output (stdout + stderr).
     */
    private String runMyThermo(String... args) throws IOException, InterruptedException {
        // Build the command array
        String[] cmd = new String[2 + args.length];
        cmd[0] = "python";            // or "python3" if that’s what worked in your terminal
        cmd[1] = "my_thermo.py";
        System.arraycopy(args, 0, cmd, 2, args.length);

        ProcessBuilder builder = new ProcessBuilder(cmd);

        // IMPORTANT: set working directory to repo root
        builder.directory(new File("/workspaces/Test-Repo---NF-Tests"));

        Process process = builder.start();

        // Read stdout
        BufferedReader outReader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = outReader.readLine()) != null) {
            out.append(line).append("\n");
        }

        // Read stderr (argparse error messages often go here)
        BufferedReader errReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder err = new StringBuilder();
        while ((line = errReader.readLine()) != null) {
            err.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        // For debugging you can print exitCode or assert if you want
        // System.out.println("Exit code = " + exitCode);

        return out.toString() + err.toString();
    }

    @Test
    public void testResetMessageIsUserFriendly() throws Exception {
        String output = runMyThermo("--reset");
        assertTrue("Should mention reset to defaults",
                output.contains("The values are reset to defaults 69, 73"));
    }

    @Test
    public void testMissingTstartShowsClearError() throws Exception {
        // tmpr without tstart -> your code calls parser.error(...)
        String output = runMyThermo("-tmpr", "70");
        assertTrue("Should tell the user that tstart is required",
                output.contains("tstart is required when setting a specific temperature"));
    }

    @Test
    public void testInvalidTimeFormatShowsFriendlyError() throws Exception {
        String output = runMyThermo("-tmpr", "70", "-tstart", "xx:yy");
        assertTrue("Should tell the user about time format",
                output.contains("Invalid time format. Use HH:MM."));
    }

    @Test
    public void testInvalidRangeShowsFriendlyError() throws Exception {
        // Below 62 and above 78
        String output = runMyThermo("-rmin", "50", "-rmax", "80");
        assertTrue("Should tell the user the allowed range",
                output.contains("Range must be between 62 and 78."));
    }

    @Test
    public void testNoArgumentsShowsHelpText() throws Exception {
        String output = runMyThermo();
        assertTrue("Help text should contain 'usage:'", output.toLowerCase().contains("usage"));
        assertTrue("Help text should mention MyThermo Command Line Thermostat",
                output.contains("MyThermo Command Line Thermostat"));
        
    }
    @Test
public void testValidRangeMessage() throws Exception {
    String output = runMyThermo("-rmin", "65", "-rmax", "72");
    assertTrue(output.contains("Range 65 - 72 is set till you decide to reset"));
    }

   @Test
public void testCommandExecutesUnderTwoSeconds() throws Exception {
    long startTime = System.currentTimeMillis();

    // INPUT:
    // User sets a valid temperature range
    String output = runMyThermo("-rmin", "70", "-rmax", "72");
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // EXPECTED RESULT:
    // Should complete in under 2000 ms (2 seconds)
    assertTrue("Program took too long to execute: " + duration + " ms", duration < 2000);

    assertTrue(output.contains("Range"), "Program should respond quickly and correctly.");

}
