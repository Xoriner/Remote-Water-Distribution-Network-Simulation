package pl.edu.pwr.mrodak.jp.components.retensionbasin;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RetensionBasinGUI extends JFrame {
    private JTextField retensionBasinName;

    private JTextField tailorNameField;
    private JTextField tailorHostField;
    private JTextField tailorPortField;

    private JTextField maxVolumeInput;
    private JTextField controlCenterNameInput;
    private JTextField incomingRiverSectionAmountInput;
    private JLabel fillingPercentageLabel;
    private JLabel waterDischargeLabel;

    private List<JTextField> incomingRiverSectionsNames = new ArrayList<>();
    private JLabel outputRiverSectionNameLabel;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private RetensionBasin retensionBasin;

    public RetensionBasinGUI() {
        setTitle("Retension Basin Configuration");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        // Retension Basin Name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Retension Basin Name:"), gbc);
        gbc.gridx = 1;
        retensionBasinName = new JTextField("RetensionBasin");
        add(retensionBasinName, gbc);

        // Tailor Name input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tailor Name:"), gbc);
        gbc.gridx = 1;
        tailorNameField = new JTextField("Tailor");
        add(tailorNameField, gbc);

        // Tailor Host input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Tailor Host:"), gbc);
        gbc.gridx = 1;
        tailorHostField = new JTextField("localhost");
        add(tailorHostField, gbc);

        // Tailor Port input
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Tailor Port:"), gbc);
        gbc.gridx = 1;
        tailorPortField = new JTextField("2000");
        add(tailorPortField, gbc);

        // Max Volume input
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Max Volume:"), gbc);
        gbc.gridx = 1;
        maxVolumeInput = new JTextField("1000");
        add(maxVolumeInput, gbc);

        // Control Center Name input
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Control Center Name:"), gbc);
        gbc.gridx = 1;
        controlCenterNameInput = new JTextField("ControlCenter");
        add(controlCenterNameInput, gbc);

        // Start Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Retension Basin and Connect with Central Control");
        startButton.addActionListener(e -> {
            try {
                startRetensionBasin();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(startButton, gbc);

        gbc.gridwidth = 1;
        // Incoming River Section Amount Input
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(new JLabel("Incoming River Section Amount:"), gbc);
        gbc.gridx = 3;
        incomingRiverSectionAmountInput = new JTextField("1");
        add(incomingRiverSectionAmountInput, gbc);

        // Add button to generate inputs for incoming river sections
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton generateInputsButton = new JButton("Generate Inputs");
        generateInputsButton.addActionListener(e -> generateIncomingRiverSectionInputs(gbc));
        add(generateInputsButton, gbc);

        // Retension Basin Info
        gbc.gridx = 0;
        gbc.gridy = 9;
        add(new JLabel("Retension Basin Info:"), gbc);

        // Filling Percentage Label
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        add(new JLabel("Filling Percentage:"), gbc);
        gbc.gridx = 1;
        fillingPercentageLabel = new JLabel("N/A");
        add(fillingPercentageLabel, gbc);

        // Water Discharge Label
        gbc.gridx = 0;
        gbc.gridy = 11;
        add(new JLabel("Water Discharge:"), gbc);
        gbc.gridx = 1;
        waterDischargeLabel = new JLabel("N/A");
        add(waterDischargeLabel, gbc);
    }

    private void startRetensionBasin() throws RemoteException {
        String name = retensionBasinName.getText();
        String tailorName = tailorNameField.getText();
        String tailorHost = tailorHostField.getText();
        int tailorPort = Integer.parseInt(tailorPortField.getText());
        int maxVolume = Integer.parseInt(maxVolumeInput.getText());
        String controlCenterName = controlCenterNameInput.getText();
        retensionBasin = new RetensionBasin(name, tailorName, tailorHost, tailorPort, maxVolume, controlCenterName);
        retensionBasin.startRetensionBasin();
        updateLabels();
    }

    private void updateLabels() {
        scheduler.scheduleAtFixedRate(() -> {
            if (retensionBasin != null) {
                try {
                    fillingPercentageLabel.setText(retensionBasin.getFillingPercentage() + "%");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                try {
                    waterDischargeLabel.setText(retensionBasin.getWaterDischarge() + " m3/s");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    private void generateIncomingRiverSectionInputs(GridBagConstraints gbc) {
        try {
            int amount = Integer.parseInt(incomingRiverSectionAmountInput.getText());
            for (int i = 0; i < amount; i++) {
                // Reset grid constraints for each label and text field
                gbc.gridx = 2;
                gbc.gridy = 2 + i;
                gbc.gridwidth = 1;
                add(new JLabel("Incoming River Section Name " + (i + 1) + ":"), gbc);

                gbc.gridx = 3;
                JTextField riverSectionName = new JTextField("RiverSection");
                incomingRiverSectionsNames.add(riverSectionName);
                add(riverSectionName, gbc);
            }

            // Reset grid constraints for the button
            gbc.gridx = 2;
            gbc.gridy = 2 + amount;
            gbc.gridwidth = 2;
            JButton connectToInputRivers = new JButton("Connect to Incoming River Sections");
            connectToInputRivers.addActionListener(e -> connectToInputRivers());
            add(connectToInputRivers, gbc);

            revalidate();
            repaint();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void connectToInputRivers() {
        readIncomingRiverSectionInputs();
        retensionBasin.assignToIncomingRiverSections();
    }

    private void readIncomingRiverSectionInputs() {
        try {
            for (int i = 0; i < incomingRiverSectionsNames.size(); i++) {
                String riverName = incomingRiverSectionsNames.get(i).getText();
                retensionBasin.addIncomingRiverSection(riverName);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for incoming river sections. Please check your entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RetensionBasinGUI::new);
    }
}
