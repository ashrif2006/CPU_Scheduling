package ui.components;

import model.Process;

import javax.swing.*;
import java.awt.*;

public class RunningProcessPanel extends JPanel {

    private final JLabel processIdLabel = new JLabel("-");
    private final JLabel arrivalTimeLabel = new JLabel("-");
    private final JLabel remainingTimeLabel = new JLabel("-");
    private final JLabel burstTimeLabel = new JLabel("-");

    public RunningProcessPanel() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder(
                "Currently Running Process"
        ));

        initializeLabels();
    }

    private void initializeLabels() {
        add(createSection("Process ID", processIdLabel));
        add(createSection("Arrival Time", arrivalTimeLabel));
        add(createSection("Remaining Time", remainingTimeLabel));
        add(createSection("Burst Time", burstTimeLabel));
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

    public void setProcess(Process process) {
        if (process == null) {
            clear();
            return;
        }

        processIdLabel.setText(String.valueOf(process.getProcessId()));

        arrivalTimeLabel.setText(
                String.valueOf(process.getArrivalTime())
        );

        remainingTimeLabel.setText(
                String.valueOf(process.getRemainingTime())
        );

        burstTimeLabel.setText(
                String.valueOf(process.getBurstTime())
        );
    }

    public void clear() {
        processIdLabel.setText("-");
        arrivalTimeLabel.setText("-");
        remainingTimeLabel.setText("-");
        burstTimeLabel.setText("-");
    }
}
