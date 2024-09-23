import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.PrintStream;

public class TestClass {
    public static A[] AA = new A[]{new B(), new C(), new F()};

    interface A {

    }

    interface D extends A {

    }
    @FunctionalInterface
    interface E extends A, D {
        void start();
    }

    public static void main(String[] args) {
        C c = new C();
        for (A a : AA) {
            if (a instanceof E) {
                ((E)a).start();
            } else if (a instanceof F) {
                F f = (F) a;
                f.print();
            }
        }
    }

    public static class B implements A, D {

        public B() {

        }
    }

    public static class C extends B implements E {

        public C() {

        }

        @Override
        public void start() {
            PrintStream ps = new PrintStream(System.out);
            ps.println("started");
        }
    }

    public static class F implements A {

        public F() {

        }

        public void print() {
            try {
                Printer printer = new Printer(PrinterJob.getPrinterJob(), new String[]{"Hi, I am a string."});
                printer.start();
            } catch (PrinterException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Printer implements Printable {
        static PrinterJob job;
        static String[] arguments;

        public Printer(PrinterJob printerJob, String... string) throws PrinterException {
            job = printerJob;
            arguments = string;
        }

        public void start() {
            job.setPrintable(this);
            try {
                main(arguments);
            } catch (PrinterException e) {
                throw new RuntimeException(e);
            }
        }

        public static void main(String[] args) throws PrinterException {
            job.print();
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) return NO_SUCH_PAGE;
            double x = 0;
            double y = 0;
            double[] xy = new double[]{pageFormat.getImageableX(),pageFormat.getImageableY()};
            Graphics2D g2d = (Graphics2D) graphics;
            for (String string : arguments) {
                g2d.translate(xy[0],xy[1]);
                g2d.draw(new Polygon());
                g2d.setFont(new Font("Serif", Font.PLAIN, 24));
                g2d.drawString(string, (int) (100 - x), (int) (100 - y));
                x -=5;
                y -=5;
                g2d.draw3DRect((int) (x + 12), (int) (y + 12), 20, 26, true);
            }

            return PAGE_EXISTS;
        }
    }
}
