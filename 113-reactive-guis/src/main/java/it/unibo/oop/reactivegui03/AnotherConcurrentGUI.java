package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final CounterAgent counterAgent = new CounterAgent();
        final ClosingAgent closingAgent = new ClosingAgent(counterAgent); 
        new Thread(counterAgent).start();
        new Thread(closingAgent).start();
        stop.addActionListener((e) -> stopExecution(counterAgent));
        up.addActionListener((e) -> counterAgent.upCounting());
        down.addActionListener((e) -> counterAgent.downCounting());
    }

    private void stopExecution(final CounterAgent agent) {
        agent.stopCounting();
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }

    /*
     * The counter agent is implemented as a nested class. This makes it
     * invisible outside and encapsulated.
     */
    private final class CounterAgent implements Runnable {
        private volatile boolean stop;
        private boolean up = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (this.up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void upCounting() {
            this.up = true;
        }

        public void downCounting() {
            this.up = false;
        }
    }

    private final class ClosingAgent implements Runnable {
        private static final int CLOSING_TIME = 10_000;
        private final CounterAgent agent;

        ClosingAgent(final CounterAgent agent) {
            this.agent = agent;
        } 

        @Override
        public void run() {
            try {
                Thread.sleep(CLOSING_TIME);
                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.stopExecution(agent));
            } catch (InvocationTargetException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }
}
