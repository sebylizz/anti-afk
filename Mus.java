import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Mus {
    private static final int SHAKE_DISTANCE = 10;
    private static final int SHAKE_TIME = 500;

    private static volatile boolean running = false;
    private static Robot robot;

    public static void main(String[] args) {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println("Failed to create Robot instance: " + e.getMessage());
            System.exit(1);
        }

        JFrame frame = new JFrame("AAU mus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(170, 100);
        frame.setLayout(new FlowLayout());

        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        JTextField timeInput = new JTextField("60");
        timeInput.setPreferredSize(new Dimension(50, 25)); // Set preferred size
        JLabel desc = new JLabel("sekunder");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    running = true;
                    int temp = Integer.valueOf(timeInput.getText());
                    if(temp < 5){timeInput.setText("5"); temp = 5;}
                    startShaker(temp*1000, timeInput);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = false;
            }
        });

        frame.add(timeInput);
        frame.add(desc);
        frame.add(startButton);
        frame.add(stopButton);

        frame.setVisible(true);
    }

    private static void startShaker(int interval, JTextField timeInput) {
        Thread autoShakeThread = new Thread(new Runnable() {
            public void run() {
                long lastMoveTime = System.currentTimeMillis();
                Point lastPoint = MouseInfo.getPointerInfo().getLocation();

                while (running) {
                    Point cur = MouseInfo.getPointerInfo().getLocation();

                    if (!cur.equals(lastPoint)) {
                        lastMoveTime = System.currentTimeMillis();
                        lastPoint = cur;
                    }

                    timeInput.setText(Integer.toString(Math.round(interval - (System.currentTimeMillis() - lastMoveTime))/1000));

                    try {
                        if (System.currentTimeMillis() - lastMoveTime >= interval) {
                            lastMoveTime = System.currentTimeMillis();
                            cur = MouseInfo.getPointerInfo().getLocation();
                            for(int i = 0; i < SHAKE_TIME / 100; i++){
                                robot.mouseMove((int) (cur.getX() - SHAKE_DISTANCE), (int) cur.getY());
                                sleep(50);
                                robot.mouseMove((int) cur.getX(), (int) cur.getY());
                                sleep(50);
                            }
                        }

                        Thread.sleep(500);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    }
                }
            });
            autoShakeThread.start();
        }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}