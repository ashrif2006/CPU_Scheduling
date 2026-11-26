package ui.components;

import java.awt.*;
import javax.swing.*;

public class SchedulerStatisticsPanel extends JPanel {

    private final JLabel averageWaitingTimeLabel =
            new JLabel("-");

    private final JLabel averageTurnaroundTimeLabel =
            new JLabel("-");

    public SchedulerStatisticsPanel() {
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createTitledBorder(
                "Scheduling Statistics"
        ));

        initializeLabels();
    }

    private void initializeLabels() {
        add(createSection(
                "Average Waiting Time",
                averageWaitingTimeLabel
        ));

        add(createSection(
                "Average Turnaround Time",
                averageTurnaroundTimeLabel
        ));
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

    public void setStatistics(
            double avgWaiting,
            double avgTurnaround
    ) {
        averageWaitingTimeLabel.setText(
                String.format("%.2f", avgWaiting)
        );

        averageTurnaroundTimeLabel.setText(
                String.format("%.2f", avgTurnaround)
        );
    }

    public void clear() {
        averageWaitingTimeLabel.setText("-");
        averageTurnaroundTimeLabel.setText("-");
    }
}