import javax.swing.*;
import java.awt.*;

public class TestClass {

    public static void main(String[] args) {

        Font font = new Font("Serif", Font.PLAIN, 24);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);

        JFrame frame = new JFrame("My Final Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 250);

        JLabel label = new JLabel("<html>I have reached my final form twin♡<br>" +
                "I've become a Java code twin♡<br>" +
                "Come be compiled into bytecode and get executed by the Java Virtual Machine with me twin♡</html>");
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        frame.add(label);

        frame.setVisible(true);
    }

}
