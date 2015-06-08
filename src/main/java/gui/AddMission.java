package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import secret_service.Mission;
import secret_service.MissionManager;
import secret_service.MissionManagerImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by Martina on 3.6.2015.
 */
public class AddMission {
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

    public AddMission(Mission mission, MissionTableModel model, JFrame iFrame) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        this.model = model;
        manager = new MissionManagerImpl(ds);

        addMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int day = Integer.parseInt(missionCompletedDayCombo.getSelectedItem().toString());
                int month = Integer.parseInt(missionCompletedMonthCombo.getSelectedItem().toString());
                int year = Integer.parseInt(missionCompletedYearCombo.getSelectedItem().toString());
                boolean isFinished = missionCompletedCheckBox.isSelected();

                LocalDate date = null;
                try {
                    date = isFinished ? LocalDate.of(year, month, day) : null;
                } catch (DateTimeException e1) {
                    e1.printStackTrace();
                }

                if (missionTitleTextField.getText().equals("") || missionCountryTextField.getText().equals("")) {
                    JFrame frame3 = new JFrame();
                    JOptionPane.showMessageDialog(frame3,bundle.getString("ErrorMissionNotFilled"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFrame frame2 = new JFrame();
                Object[] options = {bundle.getString("Yes"),
                        bundle.getString("No")};
                int n = JOptionPane.showOptionDialog(frame2,
                        bundle.getString("CreateMissionQuestion"),
                        bundle.getString("CreateMission"),
                        JOptionPane.YES_OPTION,
                        JOptionPane.NO_OPTION,
                        null,
                        options,
                        options[1]);
                if (n == JOptionPane.NO_OPTION) {
                    return;
                }

                mission.setTitle(missionTitleTextField.getText());
                mission.setDescription(missionDescriptionTextField.getText());
                mission.setRequiredClearance(Integer.parseInt(missionLevelCombo.getSelectedItem().toString()));
                mission.setCountry(missionCountryTextField.getText());
                mission.setDateOfCompletion(date);

                AddMissionSwingWorker addMissionSwingWorker = new AddMissionSwingWorker(mission);
                addMissionSwingWorker.execute();

                iFrame.dispose();
            }
        });
    }

    private class AddMissionSwingWorker extends SwingWorker<Boolean, Void> {

        private Mission mission;

        public AddMissionSwingWorker(Mission mission) {
            this.mission = mission;
        }

        @Override
        protected Boolean doInBackground() {
            try {
                manager.createMission(mission);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, bundle.getString("ErrorCreateMission"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        protected void done() {
            try {
                if (get()) {
                    model.addMission(mission);
                }
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}