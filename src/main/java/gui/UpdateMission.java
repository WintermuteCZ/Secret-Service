package gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import secret_service.MissionManager;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Martina on 3.6.2015.
 */
public class UpdateMission {
    private JComboBox missionLevelCombo;
    private JButton addMissionButton;
    private JTextField missionDescriptionTextField;
    private JTextField missionCountryTextField;
    private JTextField missionTitleTextField;
    private JComboBox missionCompletedYearCombo;
    private JComboBox missionCompletedMonthCombo;
    private JComboBox missionCompletedDayCombo;
    private JCheckBox missionCompletedCheckBox;
    private JPanel panel1;

    public JPanel getPanel1() {
        return panel1;
    }

    private MissionManager manager;
    public static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());
    private static Logger log = LoggerFactory.getLogger(BaseForm.class);
    private MissionTableModel model;

}
