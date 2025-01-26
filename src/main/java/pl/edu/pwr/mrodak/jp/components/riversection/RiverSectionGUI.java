package pl.edu.pwr.mrodak.jp.components.riversection;

import pl.edu.pwr.mrodak.jp.components.observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RiverSectionGUI extends JFrame implements Observer {
    private JTextField riverSectionDelay;
    private JTextField riverSectionName;

    private JTextField tailorNameField;
    private JTextField tailorHostField;
    private JTextField tailorPortField;

    private JTextField environmentNameField;
    private JTextField inputBasinNameField;

    private JLabel outputBasinNameLabel;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private RiverSection riverSection;

    public RiverSectionGUI() {
        setTitle("River Section Configuration");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // River Section Name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("River Section Name:"), gbc);
        gbc.gridx = 1;
        riverSectionName = new JTextField("RiverSection");
        add(riverSectionName, gbc);

        // River Section Delay input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("River Section Delay:"), gbc);
        gbc.gridx = 1;
        riverSectionDelay = new JTextField("500");
        add(riverSectionDelay, gbc);

        // Tailor Name input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Tailor Name:"), gbc);
        gbc.gridx = 1;
        tailorNameField = new JTextField("Tailor");
        add(tailorNameField, gbc);

        // Tailor Host input
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Tailor Host:"), gbc);
        gbc.gridx = 1;
        tailorHostField = new JTextField("localhost");
        add(tailorHostField, gbc);

        // Tailor Port input
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Tailor Port:"), gbc);
        gbc.gridx = 1;
        tailorPortField = new JTextField("2000");
        add(tailorPortField, gbc);

        //Environment Name input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Environment Name:"), gbc);
        gbc.gridx = 1;
        environmentNameField = new JTextField("Environment");
        add(environmentNameField, gbc);

        //Input Basin Name input
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Input Basin Name:"), gbc);
        gbc.gridx = 1;
        inputBasinNameField = new JTextField("InputBasin");
        add(inputBasinNameField, gbc);

        //Start Button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start River Section");
        startButton.addActionListener(e -> {
            try {
                startRiverSection();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(startButton, gbc);

        //Output Basin Name Label
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("Output Basin Name:"), gbc);
        gbc.gridx = 1;
        outputBasinNameLabel = new JLabel("N/A");
        add(outputBasinNameLabel, gbc);
    }

    private void startRiverSection() throws RemoteException {
        if (riverSection != null) {
            throw new IllegalStateException("River Section already started");
        }

        String riverSectionName = this.riverSectionName.getText();
        int riverSectionDelay = Integer.parseInt(this.riverSectionDelay.getText());
        String tailorName = tailorNameField.getText();
        String tailorHost = tailorHostField.getText();
        int tailorPort = Integer.parseInt(tailorPortField.getText());
        String environmentName = environmentNameField.getText();
        String inputBasinName = inputBasinNameField.getText();

        riverSection = new RiverSection(riverSectionName, riverSectionDelay, tailorName, tailorHost, tailorPort, environmentName, inputBasinName);
        riverSection.startRiverSection();
        riverSection.addObserver(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RiverSectionGUI::new);}

    @Override
    public void update(String name, String stringInfo, int intInfo) {
        SwingUtilities.invokeLater(() -> {
            outputBasinNameLabel.setText(name);
        });
    }
}
