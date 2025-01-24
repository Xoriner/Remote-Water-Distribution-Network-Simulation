package pl.edu.pwr.mrodak.jp.tailor;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TailorGUI extends JFrame {
    private JTextField TailorHostField;
    private JTextField TailorPortField;

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

        // Tailor Host input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Tailor Host:"), gbc);
        gbc.gridx = 1;
        TailorHostField = new JTextField(ipAddress);
        add(TailorHostField, gbc);

        // Tailor Port input
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Tailor Port:"), gbc);
        gbc.gridx = 1;
        TailorPortField = new JTextField("2000");
        add(TailorPortField, gbc);

        // Start button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Tailor");
        startButton.addActionListener(e -> startTailor());
        add(startButton, gbc);
    }

    private void startTailor() {
        String host = TailorHostField.getText();
        int port = Integer.parseInt(TailorPortField.getText());
        Tailor tailor = new Tailor(host, port);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TailorGUI gui = new TailorGUI();
        });
    }
}
