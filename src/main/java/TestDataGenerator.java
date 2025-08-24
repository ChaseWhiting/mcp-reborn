public class TestDataGenerator {
    public static void main(String[] args) throws Exception {
        // Example: --input mydata --output generated --client --server
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");
        String outputDir = "generated_" + sdf.format(new java.util.Date());

        String[] generatorArgs = {
            "--input", "mydata",
            "--output", outputDir,
            "--client",
            "--server"
        };
        net.minecraft.data.Main.main(generatorArgs); // Replace with your main class name
    }
}
