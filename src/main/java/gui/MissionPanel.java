package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.muni.fi.pv168.secret_service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by Martina on 3.6.2015.
 */
public class MissionPanel {
    private JPanel panel1;
    private JTable table1;
    private JRadioButton allMissionsRadioButton;
    private JRadioButton availableMissionsRadioButton;
    private JRadioButton finishedMissionsRadioButton;
    private JButton missionAddButton;
    private JButton missionUpdateButton;
    private JButton missionDeleteButton;

    private MissionTableModel missionModel = new MissionTableModel();
    private MissionManager missionManager;

    public static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());
    private static Logger log = LoggerFactory.getLogger(BaseForm.class);
    public JPanel getPanel1() {
        return panel1;
    }

    public MissionPanel() {
        table1.setModel(new MissionTableModel());
        this.missionModel = (MissionTableModel) table1.getModel();
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        missionManager = new MissionManagerImpl(ds);

        ListAllMissionsSwingWorker listAllMissionsSwingWorker = new ListAllMissionsSwingWorker();
        listAllMissionsSwingWorker.execute();

        allMissionsRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("showAllRadio({})");
                ListAllMissionsSwingWorker listAllMissionsSwingWorker = new ListAllMissionsSwingWorker();
                listAllMissionsSwingWorker.execute();
            }
        });

        availableMissionsRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("showAvailableRadio({})");
                ListAvailableMissionsSwingWorker listAvailableMissionsSwingWorker = new ListAvailableMissionsSwingWorker();
                listAvailableMissionsSwingWorker.execute();
            }
        });

        finishedMissionsRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("showCompletedRadio({})");
                ListCompletedMissionsSwingWorker listCompletedMissionsSwingWorker = new ListCompletedMissionsSwingWorker();
                listCompletedMissionsSwingWorker.execute();
            }
        });

        missionDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("deleteMissionButton({})");
                int count = table1.getSelectedRowCount();
                JFrame frame = new JFrame();
                if (count != 1) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("ErrorMustSelect"));
                    return;
                }

                int row = table1.getSelectedRow();
                Mission mission = missionManager.findMissionByID((Long) table1.getValueAt(row, 0));

                JFrame frame2 = new JFrame();
                Object[] options = {bundle.getString("Yes"),
                        bundle.getString("No")};
                int n = JOptionPane.showOptionDialog(frame2,
                        bundle.getString("DeleteMissionQuestion") + " " + mission.getTitle(),
                        bundle.getString("DeleteMission"),
                        JOptionPane.YES_OPTION,
                        JOptionPane.NO_OPTION,
                        null,
                        options,
                        options[1]);
                if (n != JOptionPane.YES_OPTION) {
                    return;
                }

                DeleteMissionSwingWorker deleteMissionSwingWorker = new DeleteMissionSwingWorker(mission);
                deleteMissionSwingWorker.execute();
            }
        });

        missionAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mission mission = new Mission();
                JFrame iFrame = new JFrame();
                iFrame.setTitle(bundle.getString("CreateMission"));
                iFrame.add(new AddMission(mission, missionModel, iFrame).getPanel1());
                iFrame.setContentPane(new AddMission(mission, missionModel, iFrame).getPanel1());
                iFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                iFrame.setPreferredSize(new Dimension(730, 180));

                iFrame.pack();
                iFrame.setLocationRelativeTo(null);
                iFrame.setVisible(true);
            }
        });

        missionUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("updateMissionButton({})");
                JFrame frame = new JFrame();
                int count = table1.getSelectedRowCount();
                if(count != 1)
                {
                    JOptionPane.showMessageDialog(frame,bundle.getString("ErrorMustSelect"));
                    return;
                }
                int row = table1.getSelectedRow();
                Mission mission = missionManager.findMissionByID((Long) table1.getValueAt(row, 0));
                JFrame iFrame = new JFrame();
                iFrame.setTitle(bundle.getString("UpdateMission"));
                iFrame.add(new UpdateMission(mission,missionModel,iFrame).getPanel1());
                iFrame.setContentPane(new UpdateMission(mission,missionModel,iFrame).getPanel1());
                iFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                iFrame.setPreferredSize(new Dimension(730, 180));

                iFrame.pack();
                iFrame.setLocationRelativeTo(null);
                iFrame.setVisible(true);
            }
        });
    }

    private class DeleteMissionSwingWorker extends SwingWorker<Boolean, Void> {

        private Mission mission;

        public DeleteMissionSwingWorker(Mission mission) {
            this.mission = mission;
        }

        @Override
        protected Boolean doInBackground() {
            try {
                missionManager.deleteMission(mission);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, bundle.getString("ErrorDeleteMission"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        protected void done() {
            try {
                if (get()) {
                    missionModel.removeMission(mission);
                }
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    private class ListAllMissionsSwingWorker extends SwingWorker<List<Mission>, Void> {

        @Override
        protected List<Mission> doInBackground() throws Exception {
            return missionManager.findAllMissions();
        }

        @Override
        protected void done() {
            missionModel.removeAllMissions();
            try {
                for (Mission mission : get()) {
                    missionModel.addMission(mission);
                }

            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());

            }
        }
    }

    private class ListAvailableMissionsSwingWorker extends SwingWorker<List<Mission>, Void> {

        @Override
        protected List<Mission> doInBackground() throws Exception {
            return missionManager.findAvailableMissions();
        }

        @Override
        protected void done() {
            missionModel.removeAllMissions();
            try {
                for (Mission mission : get()) {
                    missionModel.addMission(mission);
                }

            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());

            }
        }
    }

    private class ListCompletedMissionsSwingWorker extends SwingWorker<List<Mission>, Void> {

        @Override
        protected List<Mission> doInBackground() throws Exception {
            return missionManager.findCompletedMissions();
        }

        @Override
        protected void done() {
            missionModel.removeAllMissions();
            try {
                for (Mission mission : get()) {
                    missionModel.addMission(mission);
                }

            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());

            }
        }
    }
}
