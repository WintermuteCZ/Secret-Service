package gui;

import cz.muni.fi.pv168.secret_service.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public UpdateMission(Mission mission, MissionTableModel model, JFrame iFrame) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        this.model = model;
        manager = new MissionManagerImpl(ds);

        missionTitleTextField.setText(mission.getTitle());
        missionCountryTextField.setText(mission.getCountry());
        missionDescriptionTextField.setText(mission.getDescription());

        missionLevelCombo.setSelectedItem(Integer.toString(mission.getRequiredClearance()));

        if (mission.getDateOfCompletion() == null) {
            missionCompletedCheckBox.setSelected(false);
        }
        else {
            missionCompletedCheckBox.setSelected(true);
            missionCompletedDayCombo.setSelectedItem(Integer.toString(mission.getDateOfCompletion().getDayOfMonth()));
            missionCompletedMonthCombo.setSelectedItem(Integer.toString(mission.getDateOfCompletion().getMonthValue()));
            missionCompletedYearCombo.setSelectedItem(Integer.toString(mission.getDateOfCompletion().getYear()));
        }
        addMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int completedDay = Integer.parseInt(missionCompletedDayCombo.getSelectedItem().toString());
                int completedMonth = Integer.parseInt(missionCompletedMonthCombo.getSelectedItem().toString());
                int completedYear = Integer.parseInt(missionCompletedYearCombo.getSelectedItem().toString());
                boolean isCompleted = missionCompletedCheckBox.isSelected();

                LocalDate completed = null;
                try {
                    completed = isCompleted ? LocalDate.of(completedYear, completedMonth, completedDay) : null;
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
                        bundle.getString("UpdateMissionQuestion") + " " + mission.getTitle(),
                        bundle.getString("UpdateMission"),
                        JOptionPane.YES_OPTION,
                        JOptionPane.NO_OPTION,
                        null,
                        options,
                        options[1]);
                if (n != JOptionPane.YES_OPTION) {
                    return;
                }

                mission.setTitle(missionTitleTextField.getText());
                mission.setCountry(missionCountryTextField.getText());
                mission.setDescription(missionDescriptionTextField.getText());
                mission.setRequiredClearance(Integer.parseInt(missionLevelCombo.getSelectedItem().toString()));
                mission.setDateOfCompletion(completed);

                UpdateMissionSwingWorker updateMissionSwingWorker = new UpdateMissionSwingWorker(mission);
                updateMissionSwingWorker.execute();

                iFrame.dispose();
            }
        });
    }

    private class UpdateMissionSwingWorker extends SwingWorker<Boolean, Void> {

        private Mission mission;

        public UpdateMissionSwingWorker(Mission mission) {
            this.mission = mission;
        }

        @Override
        protected Boolean doInBackground() {
            try {
                manager.updateMission(mission);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, bundle.getString("ErrorUpdateMission"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        protected void done() {
            try {
                if (get()) {
                    model.updateMission(mission);
                }
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
