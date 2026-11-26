package ui.components;

import java.awt.*;
import javax.swing.*;
import scheduler.SchedulerStatus;

public class SchedulerHeaderPanel extends JPanel {

    private final JButton startButton = new JButton("Start");
    private final JLabel tickLabel = new JLabel("0");
    private final JLabel schedulerNameLabel = new JLabel();
    private final JLabel timeQuantumLabel = new JLabel("0");
    private final JLabel contexSwitchLabel = new JLabel("0");
    private final JLabel stateLabel = new JLabel();

    private Runnable onStart;

    public SchedulerHeaderPanel(String schedulerName, int quantum, int cs, Runnable onStart) {
        setLayout(new GridLayout(1, 6, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Scheduler Info"));

        this.onStart = onStart;

        schedulerNameLabel.setText(schedulerName);
        initializeButton();
        initializeLabels();
        timeQuantumLabel.setText(quantum + "");
        contexSwitchLabel.setText(cs + "");

        setState(SchedulerStatus.IDLE);
    }

    private void initializeLabels() {
        add(createSection("Current Tick", tickLabel));
        add(createSection("Scheduler", schedulerNameLabel));
        add(createSection("Context Switch", contexSwitchLabel));
        add(createSection("Time Quantum", timeQuantumLabel));
        add(createSection("State", stateLabel));
    }

    private JPanel createSection(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);

        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(
                valueLabel.getFont().deriveFont(Font.BOLD, 18f)
        );

        panel.add(titleLabel);
        panel.add(valueLabel);

        return panel;
    }

    private void initializeButton() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        startButton.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(startButton);
        add(panel);
        startButton.addActionListener(e -> {
            onStart.run();
            startButton.setEnabled(false);
        });
    }

    public void setTick(int tick) {
        tickLabel.setText(String.valueOf(tick));
    }

    public void setState(SchedulerStatus state) {
        switch (state) {
            case PROCESSING:
                stateLabel.setText("Running");
                stateLabel.setForeground(new Color(0, 128, 0));
                break;

            case IDLE:
                stateLabel.setText("Idle");
                stateLabel.setForeground(Color.GRAY);
                break;

            case SWITCHING:
                stateLabel.setText("Context Switching");
                stateLabel.setForeground(new Color(200, 120, 0));
                break;

            default:
                stateLabel.setText("Completed");
                stateLabel.setForeground(new Color(0, 100, 200));
                break;
        }
    }

    public void update(int tick, SchedulerStatus state) {
        setTick(tick);
        setState(state);
    }
}
