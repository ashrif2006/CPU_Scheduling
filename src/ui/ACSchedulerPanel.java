package ui;

import java.awt.*;
import javax.swing.*;
import model.SchedulerConfiguration;
import model.SchedulerSnapshot;
import scheduler.ACSScheduler;
import scheduler.Scheduler;
import simulation.SnapshotBuffer;
import ui.components.*;

public class ACSchedulerPanel extends JPanel {

    private final SchedulerHeaderPanel headerPanel;

    private final RunningProcessPanel runningProcessPanel
            = new RunningProcessPanel();

    private final ProcessQueuePanel arrivingQueuePanel
            = new ProcessQueuePanel("Arriving Queue");

    private final ProcessQueuePanel tier1QueuePanel
            = new ProcessQueuePanel("Tier 1 Queue");

    private final ProcessQueuePanel tier2QueuePanel
            = new ProcessQueuePanel("Tier 2 Queue");

    private final ProcessResultsPanel resultsPanel
            = new ProcessResultsPanel();

    private final SchedulerStatisticsPanel statisticsPanel
            = new SchedulerStatisticsPanel();

    private final JPanel contentPanel = new JPanel();

    private final SchedulerConfiguration configuration;

    public ACSchedulerPanel(SchedulerConfiguration configuration) {
        this.configuration = configuration;
        headerPanel = new SchedulerHeaderPanel("AC Scheduler", configuration.getTimeQuantum(), configuration.getContextSwitchTime(), this::startScheduling);
        setLayout(new BorderLayout());

        initializeContent();
        initializeScrollPane();
        arrivingQueuePanel.setProcesses(configuration.getProcesses());

    }

    private void initializeContent() {
        contentPanel.setLayout(
                new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        );

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        addSection(headerPanel);
        addSection(runningProcessPanel);

        initializeQueues();
        initializeResults();
    }

    private void initializeQueues() {
        addSection(arrivingQueuePanel);

        JPanel tiersPanel = new JPanel(
                new GridLayout(1, 2, 10, 0)
        );

        tiersPanel.add(tier1QueuePanel);
        tiersPanel.add(tier2QueuePanel);

        addSection(tiersPanel);
    }

    private void initializeResults() {
        addSection(resultsPanel);
        addSection(statisticsPanel);
    }

    private void initializeScrollPane() {
        JScrollPane scrollPane = new JScrollPane(contentPanel);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addSection(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                panel.getPreferredSize().height
        ));

        if (contentPanel.getComponentCount() > 0) {
            contentPanel.add(Box.createVerticalStrut(10));
        }

        contentPanel.add(panel);
    }

    private void startScheduling() {
        SnapshotBuffer buffer = new SnapshotBuffer();
        Thread schedulerThread = new Thread(() -> {
            Scheduler scheduler = new ACSScheduler(buffer);
            scheduler.simulate(configuration.getProcesses(), configuration.getTimeQuantum(), configuration.getContextSwitchTime());
        });
        schedulerThread.start();
        Thread visualizationThread = new Thread(() -> {
            while (true) {
                try {
                    SchedulerSnapshot snapshot = buffer.consume();
                    updateUI(snapshot);
                    if (snapshot.isFinal()) {
                        break;
                    }
                    Thread.currentThread().sleep(500);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        visualizationThread.start();

    }

    private void updateUI(SchedulerSnapshot snapshot) {
        headerPanel.update(snapshot.tick, snapshot.status);
        runningProcessPanel.setProcess(snapshot.runningProcess);
        arrivingQueuePanel.setProcesses(snapshot.queues.get(0).processes);
        tier1QueuePanel.setProcesses(snapshot.queues.get(1).processes);
        tier2QueuePanel.setProcesses(snapshot.queues.get(2).processes);
        if (snapshot.isFinal()) {
            resultsPanel.setResults(snapshot.result.processes);
            statisticsPanel.setStatistics(snapshot.result.averageWaitingTime(), snapshot.result.averageTurnaroundTime());
        }
    }

}
