package mcp.client;

import java.io.IOException;
import java.util.Arrays;
import net.minecraft.data.Main;  // This is the `Main` class you shared from the data generation system

public class DataGenerationStart {

    public static void main(String[] args) throws IOException {
        /*
         * Call Minecraft's data generation system to generate assets (loot tables, recipes, etc.)
         * Here we provide necessary arguments like --output and --input directories.
         * The flags like --client, --server, --validate control what gets generated.
         */
        
        // Define the output folder where the generated data will go
        String outputDir = "generated";
        
        // Optionally, define an input folder (where you might have existing resources to pull from)
        String inputDir = "src/main/resources";

        // Command-line arguments for the data generation
        String[] dataGenArgs = new String[]{
                "--output", outputDir,     // Specifies the output folder
                "--input", inputDir,       // Specifies the input folder (optional)
                "--client",                // Generate client-side resources (e.g., blockstates, models)
                "--server",                // Generate server-side resources (e.g., loot tables, recipes)
                "--validate"               // Validates the data generated to catch errors
        };
        
        // Call the data generation system
        Main.main(concat(dataGenArgs, args));  // Merge any args passed from the command line
    }

    // Utility method to concatenate two arrays
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
