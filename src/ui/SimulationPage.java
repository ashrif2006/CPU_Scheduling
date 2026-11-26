package ui;

import javax.swing.*;

import model.SchedulerConfiguration;

import java.awt.*;

public class SimulationPage extends JPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel schedulerContainer = new JPanel(cardLayout);

    private final JPanel acPanel = new JPanel();
    private final JPanel sjfPanel = new JPanel();

    private final SchedulerConfiguration configuration;

    public SimulationPage(SchedulerConfiguration configuration) {
        this.configuration = configuration;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeNavigation();
        initializeSchedulerContainer();
    }

    private void initializeNavigation() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        JToggleButton acButton = new JToggleButton("AC Scheduler");
        JToggleButton sjfButton = new JToggleButton("SJF Scheduler");

        ButtonGroup group = new ButtonGroup();

        group.add(acButton);
        group.add(sjfButton);

        acButton.setSelected(true);

        acButton.addActionListener(e
                -> cardLayout.show(schedulerContainer, "AC"));

        sjfButton.addActionListener(e
                -> cardLayout.show(schedulerContainer, "SJF"));

        panel.add(acButton);
        panel.add(sjfButton);

        add(panel, BorderLayout.NORTH);
    }

    private void initializeSchedulerContainer() {
        acPanel.add(new JLabel("AC Scheduler Panel"));
        sjfPanel.add(new JLabel("SJF Scheduler Panel"));

        schedulerContainer.add(new ACSchedulerPanel(configuration), "AC");
        schedulerContainer.add(sjfPanel, "SJF");

        cardLayout.show(schedulerContainer, "AC");

        add(schedulerContainer, BorderLayout.CENTER);
    }
}
