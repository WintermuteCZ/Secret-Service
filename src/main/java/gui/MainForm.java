package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Martina on 6.5.2015.
 */
public class MainForm {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Titulek okna");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new MainForm().panel1);
                frame.setPreferredSize(new Dimension(800,600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTable agentsTable;
    private JPanel agentPanel;
    private JTextField agentNameTextField;
    private JTextField agentGenderTextField;
    private JTextField agentLevelTextField;
    private JSpinner agentDeathYearSpinner;
    private JButton addAgentButton;
    private JTextField missionDescriptionTextField;
    private JButton addMissionButton;
    private JTable missionsTable;
    private JTextField missionTitleTextField;
    private JPanel missionPanel;
    private JTextField missionCountryTextField;
    private JTextField missionLevelTextField;
    private JButton sendButton;
    private JButton removeAgentFromMissionButton;
    private JComboBox sendMissionComboBox;
    private JComboBox removeMissionComboBox;
    private JComboBox sendAgentComboBox;
    private JEditorPane loremIpsumEditorPane;
    private JRadioButton allAgentsRadioButton;
    private JRadioButton deadAgentsRadioButton;
    private JRadioButton aliveAgentsRadioButton;
    private JRadioButton allMissionsRadioButton;
    private JPanel agencyPanel;
    private JSpinner agentBirthYearSpinner;
    private JSpinner agentBirthDaySpinner;
    private JSpinner agentBirthMonthSpinner;
    private JSpinner agentDeathDaySpinner;
    private JSpinner agentDeathMonthSpinner;
    private JSpinner missionCompletedYearSpinner;
    private JSpinner missionCompletedMonthSpinner;
    private JSpinner missionCompletedDaySpinner;
    private JRadioButton availableMissionsRadioButton;
    private JRadioButton completedMissionsRadioButton;


}


