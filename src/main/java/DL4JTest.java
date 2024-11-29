import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class DL4JTest {
    public static void main(String[] args) {
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .list()
                .layer(new DenseLayer.Builder().nIn(2).nOut(3)
                        .activation(Activation.RELU).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.SIGMOID) // Changed from Softmax to Sigmoid
                        .nIn(3).nOut(1).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();

        System.out.println("Deeplearning4j Setup Successful!");
    }
}
