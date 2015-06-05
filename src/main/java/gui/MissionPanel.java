package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import secret_service.*;

import javax.swing.*;
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
    private JButton missionShowButton;
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
        //table1.setDefaultRenderer(Color.class, new ColorCellRenderer());
        this.missionModel = (MissionTableModel) table1.getModel();
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        missionManager = new MissionManagerImpl(ds);
        missionManager.createMission(new Mission(null, "Hard mission", "Russia", "really hard", LocalDate.of(2009,5,6), 3));

        ListAllMissionsSwingWorker listAllMissionsSwingWorker = new ListAllMissionsSwingWorker();
        listAllMissionsSwingWorker.execute();

        //TODO: map buttons

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
}
