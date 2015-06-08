package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import secret_service.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Martina on 5.6.2015.
 */
public class RemoveAgentFromMission {
    private JPanel panel1;
    private JTable table1;
    private JButton removeAgent;
    private JButton backButton;

    public JPanel getPanel1() {
        return panel1;
    }

    private MissionTableModel missionModel = new MissionTableModel();
    private MissionManager missionManager;
    private SecretService secretServiceManager;
    public static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());
    private static Logger log = LoggerFactory.getLogger(BaseForm.class);

    public RemoveAgentFromMission(SecretAgent agent, JFrame iFrame) {
        table1.setModel(new MissionTableModel());
        this.missionModel = (MissionTableModel) table1.getModel();
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        missionManager = new MissionManagerImpl(ds);
        secretServiceManager = new SecretServiceImpl(ds);
        secretServiceManager.findMissionsWithAgent(agent).forEach(missionModel::addMission);


        /* Jak spravne zavrit iFrame, pokud pocet misi = 0?

        int count = table1.getRowCount();
        if (count == 0) {
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, bundle.getString("ErrorNoMissions"));
            frame.dispose();
            return;
        }*/

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iFrame.dispose();
            }
        });

        removeAgent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();
                int count = table1.getSelectedRowCount();
                if (count != 1) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("ErrorMustSelect"));
                    return;
                }
                int row = table1.getSelectedRow();
                Mission mission = missionManager.findMissionByID((Long) table1.getValueAt(row, 0));
                RemoveAgentSwingWorker removeAgentSwingWorker = new RemoveAgentSwingWorker(agent, mission);
                removeAgentSwingWorker.execute();
                iFrame.dispose();
            }
        });
    }

    private class RemoveAgentSwingWorker extends SwingWorker<Boolean, Void> {

        private SecretAgent agent;
        private Mission mission;

        public RemoveAgentSwingWorker(SecretAgent agent, Mission mission) {
            this.agent = agent;
            this.mission = mission;
        }

        @Override
        protected Boolean doInBackground() {
            try {
                secretServiceManager.removeAgentFromMission(agent, mission);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, bundle.getString("ErrorSentAgent"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        protected void done() {

        }
    }


}
