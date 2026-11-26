package ui.components;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Process;

public class ProcessQueuePanel extends JPanel {

    private final JLabel queueNameLabel = new JLabel();
    private final JLabel processCountLabel
            = new JLabel("Processes: 0");

    private final DefaultTableModel tableModel
            = new DefaultTableModel(
                    new String[]{
                        "Process ID",
                        "Arrival Time",
                        "Burst Time",
                        "Remaining Time",
                        "Priority"
                    }, 0
            ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public ProcessQueuePanel(String queueName) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEtchedBorder());

        queueNameLabel.setText(queueName);

        initializeHeader();
        initializeTable();
    }

    private void initializeHeader() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        queueNameLabel.setFont(
                queueNameLabel.getFont().deriveFont(Font.BOLD)
        );

        panel.add(queueNameLabel, BorderLayout.WEST);
        panel.add(processCountLabel, BorderLayout.EAST);

        add(panel, BorderLayout.NORTH);
    }

    private void initializeTable() {
        table.getTableHeader().setReorderingAllowed(false);

        table.setFillsViewportHeight(true);

        table.setPreferredScrollableViewportSize(
                new Dimension(500, 100)
        );

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setProcesses(List<Process> processes) {
        tableModel.setRowCount(0);

        for (Process process : processes) {
            tableModel.addRow(new Object[]{
                process.getProcessId(),
                process.getArrivalTime(),
                process.getBurstTime(),
                process.getRemainingTime(),
                process.getPriority()
            });
        }

        String label = "";
        int size = processes.size();
        if (size == 0) {
            label = "Empty";
        } else if (size == 1 || size > 9) {
            label = size + " Process";
        } else {
            label = size + " Processes";
        }

        processCountLabel.setText(label);
    }

    public void clear() {
        tableModel.setRowCount(0);
        processCountLabel.setText("Empty");
    }
}
