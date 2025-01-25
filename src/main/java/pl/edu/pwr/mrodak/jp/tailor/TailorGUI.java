package pl.edu.pwr.mrodak.jp.tailor;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.Map;

public class TailorGUI extends JFrame {
    private JTextField tailorNameField;
    private JTextField TailorHostField;
    private JTextField TailorPortField;

    private DefaultListModel<String> componentListModel;
    private JList<String> componentList;

    private Tailor tailor;

    public TailorGUI() {
        setTitle("Tailor");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        String ipAddress = getIpAddress();

        // GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tailor Name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Tailor Name:"), gbc);
        gbc.gridx = 1;
        tailorNameField = new JTextField("Tailor");
        add(tailorNameField, gbc);

        // Tailor Host input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tailor Host:"), gbc);
        gbc.gridx = 1;
        TailorHostField = new JTextField(ipAddress);
        add(TailorHostField, gbc);

        // Tailor Port input
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Tailor Port:"), gbc);
        gbc.gridx = 1;
        TailorPortField = new JTextField("2000");
        add(TailorPortField, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Tailor");
        startButton.addActionListener(e -> startTailor());
        add(startButton, gbc);

        // Component list
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(new JLabel("Registered Components:"), gbc);

        componentListModel = new DefaultListModel<>();
        componentList = new JList<>(componentListModel);
        JScrollPane scrollPane = new JScrollPane(componentList);
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Refresh button
        gbc.gridy = 6;
        gbc.weighty = 0;
        JButton refreshButton = new JButton("Refresh Component List");
        refreshButton.addActionListener(e -> refreshComponentList());
        add(refreshButton, gbc);
    }

    private void startTailor() {
        String name = tailorNameField.getText();
        String host = TailorHostField.getText();
        int port = Integer.parseInt(TailorPortField.getText());
        tailor = new Tailor(name, host, port);
        tailor.startTailor();
    }

    private String getIpAddress() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return inetAddress.getHostAddress();
    }

    private void refreshComponentList() {
        if (tailor == null) {
            JOptionPane.showMessageDialog(this, "Tailor is not running.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Get the component map from the Tailor instance
            Map<String, Remote> components = tailor.getComponentMap();

            // Update the component list in the GUI
            componentListModel.clear();
            for (Map.Entry<String, Remote> entry : components.entrySet()) {
                String name = entry.getKey();
                //TODO: Consider not using type name
                String type = entry.getValue().getClass().getSimpleName(); // Get the class name of the remote object
                componentListModel.addElement(name + " (" + type + ")");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error refreshing component list: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TailorGUI gui = new TailorGUI();
        });
    }
}
