package pl.edu.pwr.mrodak.jp.components.environment;

import pl.edu.pwr.mrodak.jp.components.controlcenter.ControlCenter;
import pl.edu.pwr.mrodak.jp.components.observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

//TODO: Implement this class
public class EnvironmentGUI extends JFrame implements Observer {
    private JTextField environmentNameField;
    private JTextField tailorNameField;
    private JTextField tailorHostField;
    private JTextField tailorPortField;

    private JTextField riverSectionNameField;
    private JTextField rainfallField;
    private DefaultListModel listModel;

    private Environment environment;

    public EnvironmentGUI() {
        setTitle("Environment");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        // Environment Name input
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Environment Name:"), gbc);
        gbc.gridx = 1;
        environmentNameField = new JTextField("Environment");
        add(environmentNameField, gbc);

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
        JButton startButton = new JButton("Start Environment");
        startButton.addActionListener(e -> {
            try {
                startEnvironment();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(startButton, gbc);

        //List of monitored river sections
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Monitored River Sections:"), gbc);

        JList<String> sectionList = new JList<>();
        listModel = new DefaultListModel<>();
        sectionList.setModel(listModel);
        JScrollPane listScrollPane = new JScrollPane(sectionList);
        listScrollPane.setPreferredSize(new Dimension(350, 300));
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(listScrollPane, gbc);

        //River Section Name input
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("River Section Name:"), gbc);
        gbc.gridx = 1;
        riverSectionNameField = new JTextField("RiverSection");
        add(riverSectionNameField, gbc);

        //Rainfall input
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        add(new JLabel("Rainfall (m3):"), gbc);
        gbc.gridx = 1;
        rainfallField = new JTextField();
        add(rainfallField, gbc);

        //Set Rainfall button
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        JButton setRainfall = new JButton("Set Rainfall");
        setRainfall.addActionListener(e -> setRainfall());
        add(setRainfall, gbc);
    }

    private void startEnvironment() throws RemoteException {
        String environmentName = environmentNameField.getText();
        String tailorName = tailorNameField.getText();
        String tailorHost = tailorHostField.getText();
        int tailorPort = Integer.parseInt(tailorPortField.getText());
        environment = new Environment(environmentName, tailorName, tailorHost, tailorPort);
        environment.addObserver(this);
        environment.startEnvironment();
    }

    private void setRainfall() {
        String riverSectionName = riverSectionNameField.getText();
        int rainfall = Integer.parseInt(rainfallField.getText());
        environment.contactRiverSectionToSetRainfall(riverSectionName, rainfall);
    }

    @Override
    public void update(String name, String stringInfo, int intInfo) {
        SwingUtilities.invokeLater(() -> {
            boolean updated = false;
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).toString().equals(name)) {
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                listModel.addElement(name);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EnvironmentGUI::new);
    }
}
