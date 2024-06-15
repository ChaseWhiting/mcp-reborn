package net.minecraft.groovy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GroovyEditorWindow {
    private JFrame frame;
    private JTextArea textArea;
    private JButton executeButton;
    private GroovyEditorCallback callback;

    public GroovyEditorWindow(GroovyEditorCallback callback) {
        this.callback = callback;
        frame = new JFrame("Groovy Script Editor");
        textArea = new JTextArea(20, 50);
        executeButton = new JButton("Execute");

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = textArea.getText();
                callback.onCodeEntered(code);
                frame.dispose();
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(executeButton, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void show() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
    }

    public interface GroovyEditorCallback {
        void onCodeEntered(String code);
    }
}
