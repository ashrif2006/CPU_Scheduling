package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import data.TestCases;
import model.Process;
import model.SchedulerConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigurationPage extends JPanel {

    private final JTextField csField = new JTextField();
    private final JTextField quantumField = new JTextField();

    private int nextProcessId = 1;

    private final DefaultTableModel tableModel
            = new DefaultTableModel(
                    new String[]{
                        "Process ID", "Arrival Time",
                        "Burst Time", "Priority"
                    }, 0
            );

    private final JTable table = new JTable(tableModel);

    private final Consumer<SchedulerConfiguration> onSubmit;

    public ConfigurationPage(Consumer<SchedulerConfiguration> onSubmit) {
        this.onSubmit = onSubmit;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeFields();
        initializeTable();
        initializeSubmitButton();
    }

    private void initializeFields() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Context Switching time"));
        panel.add(csField);

        panel.add(new JLabel("Time quantum"));
        panel.add(quantumField);

        add(panel, BorderLayout.NORTH);
    }

    private void initializeTable() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add Process");
        JButton removeButton = new JButton("Remove Selected Process");
        JButton loadButton = new JButton("Load Tests");

        addButton.addActionListener(e -> addRow());
        removeButton.addActionListener(e -> removeRow());
        loadButton.addActionListener(e -> loadFile());

        actions.add(addButton);
        actions.add(removeButton);
        actions.add(loadButton);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty("terminateEditOnFocusLost", true);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
    }

    private void initializeSubmitButton() {
        JButton button = new JButton("Submit");

        button.addActionListener(e -> submit());

        add(button, BorderLayout.SOUTH);
    }

    private void addRow() {
        tableModel.addRow(new Object[4]);
        tableModel.setValueAt(nextProcessId++, tableModel.getRowCount() - 1, 0);
    }

    private void removeRow() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            if (table.isEditing()) {
                table.getCellEditor().cancelCellEditing();
            }

            tableModel.removeRow(selectedRow);
        }
    }

    private void loadFile() {
        List<Process> tests = TestCases.getTestCase1();
        for (Process p : tests) {
            addRow();
            tableModel.setValueAt(p.getArrivalTime(), tableModel.getRowCount() - 1, 1);
            tableModel.setValueAt(p.getBurstTime(), tableModel.getRowCount() - 1, 2);
            tableModel.setValueAt(p.getPriority(), tableModel.getRowCount() - 1, 3);
        }
    }

    private void submit() {
        if (table.isEditing()) {
            if (!table.getCellEditor().stopCellEditing()) {
                showError("Invalid table cell.");
                return;
            }
        }

        SchedulerConfiguration configuration;

        try {
            int cs = parseInteger(csField.getText(), "Context Switching time");
            int tq = parseInteger(quantumField.getText(), "Time quantum");

            List<Process> processes = new ArrayList<>();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String pId = "Process " + parseInteger(tableModel.getValueAt(i, 0), tableModel.getColumnName(0));
                processes.add(new Process(
                        pId,
                        parseInteger(tableModel.getValueAt(i, 1), tableModel.getColumnName(1) + " on " + pId),
                        parseInteger(tableModel.getValueAt(i, 2), tableModel.getColumnName(2) + " on " + pId),
                        parseInteger(tableModel.getValueAt(i, 3), tableModel.getColumnName(3) + " on " + pId)
                ));
            }

            if (processes.isEmpty()) {
                showError("There are no processes Entered");
                return;
            }

            configuration = new SchedulerConfiguration(cs, tq, processes);

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        onSubmit.accept(configuration);
    }

    private int parseInteger(Object value, String fieldName) {
        try {
            return Integer.parseInt(
                    value == null ? "" : value.toString().trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fieldName + " must contain a valid integer."
            );
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
