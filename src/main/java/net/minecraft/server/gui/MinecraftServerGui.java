package net.minecraft.server.gui;

import com.google.common.collect.Lists;
import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent {
   private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private Thread logAppenderThread;
   private final Collection<Runnable> finalizers = Lists.newArrayList();
   private final AtomicBoolean isClosing = new AtomicBoolean();

   public static MinecraftServerGui showFrameFor(final DedicatedServer dedicatedServer) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception exception) {
      }

      final JFrame jframe = new JFrame("Minecraft server");
      final MinecraftServerGui minecraftservergui = new MinecraftServerGui(dedicatedServer);
      jframe.setDefaultCloseOperation(2);
      jframe.add(minecraftservergui);
      jframe.pack();
      jframe.setLocationRelativeTo((Component)null);
      jframe.setVisible(true);
      jframe.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent p_windowClosing_1_) {
            if (!minecraftservergui.isClosing.getAndSet(true)) {
               jframe.setTitle("Minecraft server - shutting down!");
               dedicatedServer.halt(true);
               minecraftservergui.runFinalizers();
            }

         }
      });
      minecraftservergui.addFinalizer(jframe::dispose);
      minecraftservergui.start();
      return minecraftservergui;
   }

   private MinecraftServerGui(DedicatedServer p_i2362_1_) {
      this.server = p_i2362_1_;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.buildChatPanel(), "Center");
         this.add(this.buildInfoPanel(), "West");
      } catch (Exception exception) {
         LOGGER.error("Couldn't build server GUI", (Throwable)exception);
      }

   }

   public void addFinalizer(Runnable p_219045_1_) {
      this.finalizers.add(p_219045_1_);
   }

   private JComponent buildInfoPanel() {
      JPanel jpanel = new JPanel(new BorderLayout());
      StatsComponent statscomponent = new StatsComponent(this.server);
      this.finalizers.add(statscomponent::close);
      jpanel.add(statscomponent, "North");
      jpanel.add(this.buildPlayerPanel(), "Center");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return jpanel;
   }

   private JComponent buildPlayerPanel() {
      JList<?> jlist = new PlayerListComponent(this.server);
      JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);
      jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return jscrollpane;
   }

   private JComponent buildChatPanel() {
      JPanel jpanel = new JPanel(new BorderLayout());
      JTextArea jtextarea = new JTextArea();
      JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
      jtextarea.setEditable(false);
      jtextarea.setFont(MONOSPACED);
      JTextField jtextfield = new JTextField();
      jtextfield.addActionListener((p_210465_2_) -> {
         String s = jtextfield.getText().trim();
         if (!s.isEmpty()) {
            this.server.handleConsoleInput(s, this.server.createCommandSourceStack());
         }

         jtextfield.setText("");
      });
      jtextarea.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent p_focusGained_1_) {
         }
      });
      jpanel.add(jscrollpane, "Center");
      jpanel.add(jtextfield, "South");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.logAppenderThread = new Thread(() -> {
         String s;
         while((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
            this.print(jtextarea, jscrollpane, s);
         }

      });
      this.logAppenderThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.logAppenderThread.setDaemon(true);
      return jpanel;
   }

   public void start() {
      this.logAppenderThread.start();
   }

   public void close() {
      if (!this.isClosing.getAndSet(true)) {
         this.runFinalizers();
      }

   }

   private void runFinalizers() {
      this.finalizers.forEach(Runnable::run);
   }

   public void print(JTextArea p_164247_1_, JScrollPane p_164247_2_, String p_164247_3_) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> {
            this.print(p_164247_1_, p_164247_2_, p_164247_3_);
         });
      } else {
         Document document = p_164247_1_.getDocument();
         JScrollBar jscrollbar = p_164247_2_.getVerticalScrollBar();
         boolean flag = false;
         if (p_164247_2_.getViewport().getView() == p_164247_1_) {
            flag = (double)jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(MONOSPACED.getSize() * 4) > (double)jscrollbar.getMaximum();
         }

         try {
            document.insertString(document.getLength(), p_164247_3_, (AttributeSet)null);
         } catch (BadLocationException badlocationexception) {
         }

         if (flag) {
            jscrollbar.setValue(Integer.MAX_VALUE);
         }

      }
   }
}