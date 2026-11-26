package ui.components;

import model.ProcessResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProcessResultsPanel extends JPanel {

    private final DefaultTableModel tableModel
            = new DefaultTableModel(
                    new String[]{
                        "Process ID",
                        "Burst Time",
                        "Arrival Time",
                        "Completion Time",
                        "Waiting Time",
                        "Turnaround Time"
                    }, 0
            ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public ProcessResultsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Process Results"));

        initializeTable();
    }

    private void initializeTable() {
        table.getTableHeader().setReorderingAllowed(false);

        table.setFillsViewportHeight(true);

        table.setPreferredScrollableViewportSize(
                new Dimension(500, 150)
        );

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setResults(List<ProcessResult> results) {
        tableModel.setRowCount(0);

        for (ProcessResult result : results) {
            tableModel.addRow(new Object[]{
                result.processId,
                result.burstTime,
                result.arrivalTime,
                result.completionTime,
                result.waitingTime(),
                result.turnaroundTime()
            });
        }
    }

    public void clear() {
        tableModel.setRowCount(0);
    }
}
