package pl.edu.pwr.mrodak.jp.components.controlcenter;

import interfaces.IControlCenter;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class ControlCenterGUI extends JFrame {
    private JTextField controlCenterNameField;
    private JTextField tailorNameField;
    private JTextField tailorHostField;
    private JTextField tailorPortField;

    private JTextField retensionBasinNameField;
    private JTextField waterDischargeField;
    private DefaultListModel<String> listModel;

    private ControlCenter controlCenter;

    public ControlCenterGUI() {
        setTitle("Control Center");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(9, 9, 9, 9);

        // Control Center Name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Control Center Name:"), gbc);
        gbc.gridx = 1;
        controlCenterNameField = new JTextField("ControlCenter");
        add(controlCenterNameField, gbc);

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

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Control Center and register with Tailor");
        startButton.addActionListener(e -> {
            try {
                startControlCenter();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(startButton, gbc);

        // List of assigned basins
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Registered Retension Basins:"), gbc);

        JList<String> basinList = new JList<>();
        listModel = new DefaultListModel<>();
        basinList.setModel(listModel);
        JScrollPane listScrollPane = new JScrollPane(basinList);
        listScrollPane.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(listScrollPane, gbc);

        // retensionBasin name input
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JLabel("RetensionBasin name:"), gbc);
        gbc.gridx = 1;
        retensionBasinNameField = new JTextField();
        add(retensionBasinNameField, gbc);

        // Water Discharge input
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        add(new JLabel("Water Discharge (m3/s):"), gbc);
        gbc.gridx = 1;
        waterDischargeField = new JTextField();
        add(waterDischargeField, gbc);

        // Set Water Discharge button
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        JButton setWaterDischargeButton = new JButton("Set Water Discharge");
        setWaterDischargeButton.addActionListener(e -> setWaterDischarge());
        add(setWaterDischargeButton, gbc);


    }

    private void startControlCenter() throws RemoteException {
        String controlCenterName = controlCenterNameField.getText();
        String tailorName = tailorNameField.getText();
        String tailorHost = tailorHostField.getText();
        int tailorPort = Integer.parseInt(tailorPortField.getText());
        controlCenter = new ControlCenter(controlCenterName, tailorName, tailorHost, tailorPort);
        controlCenter.startControlCenter();
    }

    //TODO: Implement this method
    private void setWaterDischarge() {
        String retensionBasinName = retensionBasinNameField.getText();
        int waterDischarge = Integer.parseInt(waterDischargeField.getText());
        controlCenter.contactRetensionBasinToSetWaterDischarge(retensionBasinName, waterDischarge);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ControlCenterGUI::new);
    }
}
