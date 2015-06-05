package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import secret_service.AgentManager;
import secret_service.AgentManagerImpl;
import secret_service.SecretAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.List;

/**
 * Created by Martina on 3.6.2015.
 */
public class AgentPanel {
    private JPanel panel1;
    private JTable table1;
    private JRadioButton allAgentsRadioButton;
    private JRadioButton aliveAgentsRadioButton;
    private JRadioButton deadAgentsRadioButton;
    private JButton agentShowButton;
    private JButton agentAddButton;
    private JButton agentUpdateButton;
    private JButton agentSentButton;
    private JButton agentDeleteButton;
    private JButton deleteAgentFromMissionButton;

    private AgentTableModel agentModel = new AgentTableModel();
    private AgentManager agentManager;

    public static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());
    private static Logger log = LoggerFactory.getLogger(BaseForm.class);
    public JPanel getPanel1() {
        return panel1;
    }

    public AgentPanel() {
        table1.setModel(new AgentTableModel());
        //table1.setDefaultRenderer(Color.class, new ColorCellRenderer());
        this.agentModel = (AgentTableModel) table1.getModel();
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        agentManager = new AgentManagerImpl(ds);
        agentManager.createAgent(new SecretAgent(null, "James Bond", "male", LocalDate.of(1998,5,12), null, 5));
        agentManager.createAgent(new SecretAgent(null, "James Bond", "male", LocalDate.of(1998,5,12), null, 5));
        agentManager.createAgent(new SecretAgent(null, "James Bond", "male", LocalDate.of(1998,5,12), null, 5));
        ListAllAgentsSwingWorker listAllAgentsSwingWorker = new ListAllAgentsSwingWorker();
        listAllAgentsSwingWorker.execute();

        deleteAgentFromMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("removeAgentFromMissionButton({})");
                int count = table1.getSelectedRowCount();
                JFrame frame = new JFrame();
                if (count != 1) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("ErrorMustSelect"));
                    return;
                }
                int row = table1.getSelectedRow();
                SecretAgent agent = agentManager.findAgentByID((Long) table1.getValueAt(row, 0));

                JFrame iFrame = new JFrame();
                iFrame.setTitle("Remove agent from mission");
                iFrame.add(new RemoveAgentFromMission(agent, iFrame).getPanel1());
                iFrame.setContentPane(new RemoveAgentFromMission(agent, iFrame).getPanel1());
                iFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                iFrame.setPreferredSize(new Dimension(600, 400));

                iFrame.pack();
                iFrame.setVisible(true);
            }
        });

        agentShowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("showAgentsButton({})");
                if (allAgentsRadioButton.isSelected()) {
                    ListAllAgentsSwingWorker listAllAgentsSwingWorker = new ListAllAgentsSwingWorker();
                    listAllAgentsSwingWorker.execute();
                }
                if (aliveAgentsRadioButton.isSelected()) {
                    ListAliveAgentsSwingWorker listAliveAgentsSwingWorker = new ListAliveAgentsSwingWorker();
                    listAliveAgentsSwingWorker.execute();
                }
                if (deadAgentsRadioButton.isSelected()) {
                    ListDeadAgentsSwingWorker listDeadAgentsSwingWorker = new ListDeadAgentsSwingWorker();
                    listDeadAgentsSwingWorker.execute();

                }
            }
        });

        agentSentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("sentAgentButton({})");
                int count = table1.getSelectedRowCount();
                JFrame frame = new JFrame();
                if (count != 1) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("ErrorMustSelect"));
                    return;
                }
                int row = table1.getSelectedRow();
                SecretAgent agent = agentManager.findAgentByID((Long) table1.getValueAt(row, 0));

                JFrame iFrame = new JFrame();
                iFrame.setTitle("Sent agent");
                iFrame.add(new SentAgent(agent, iFrame).getPanel1());
                iFrame.setContentPane(new SentAgent(agent, iFrame).getPanel1());
                iFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                iFrame.setPreferredSize(new Dimension(600, 400));

                iFrame.pack();
                iFrame.setVisible(true);
            }
        });

        agentDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("deleteAgentButton({})");
                int count = table1.getSelectedRowCount();
                JFrame frame = new JFrame();
                if (count != 1) {
                    JOptionPane.showMessageDialog(frame, bundle.getString("ErrorMustSelect"));
                    return;
                }
                int row = table1.getSelectedRow();
                SecretAgent agent = agentManager.findAgentByID((Long) table1.getValueAt(row, 0));
                DeleteAgentSwingWorker deleteAgentSwingWorker = new DeleteAgentSwingWorker(agent);
                deleteAgentSwingWorker.execute();
            }
        });

        agentAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SecretAgent agent = new SecretAgent();
                JFrame iFrame = new JFrame();
                iFrame.setTitle("Add agent");
                iFrame.add(new AddAgent(agent, agentModel, iFrame).getPanel1());
                iFrame.setContentPane(new AddAgent(agent, agentModel, iFrame).getPanel1());
                iFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                iFrame.setPreferredSize(new Dimension(700, 200));

                iFrame.pack();
                iFrame.setVisible(true);
            }
        });

        agentUpdateButton.addActionListener(new ActionListener() {
            @Override
        public void actionPerformed(ActionEvent e) {
                log.debug("updateSpyButton({})");
                JFrame frame = new JFrame();
                int count = table1.getSelectedRowCount();
                if(count != 1)
                {
                    JOptionPane.showMessageDialog(frame,bundle.getString("ErrorMustSelect"));
                    return;
                }
                int row = table1.getSelectedRow();
                SecretAgent agent = agentManager.findAgentByID((Long) table1.getValueAt(row, 0));
                JFrame iFrame = new JFrame();
                iFrame.setTitle(bundle.getString("UpdateAgent"));
                iFrame.add(new UpdateAgent(agent,agentModel,iFrame).getPanel1());
                iFrame.setContentPane(new UpdateAgent(agent,agentModel,iFrame).getPanel1());
                iFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                iFrame.setPreferredSize(new Dimension(600, 200));

                iFrame.pack();
                iFrame.setVisible(true);
            }
        });
    }

    private class DeleteAgentSwingWorker extends SwingWorker<Boolean, Void> {

        private SecretAgent agent;

        public DeleteAgentSwingWorker(SecretAgent agent) {
            this.agent = agent;
        }

        @Override
        protected Boolean doInBackground() {
            try {
                agentManager.deleteAgent(agent);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, bundle.getString("ErrorDeleteAgent"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        protected void done() {
            try {
                if (get()) {
                    agentModel.removeAgent(agent);
                }
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    private class ListAllAgentsSwingWorker extends SwingWorker<List<SecretAgent>, Void> {

        @Override
        protected List<SecretAgent> doInBackground() throws Exception {
            return agentManager.findAllAgents();
        }

        @Override
        protected void done() {
            agentModel.removeAllAgents();
            try {
                for (SecretAgent agent : get()) {
                    agentModel.addAgent(agent);
                }

            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());

            }
        }
    }
    private class ListAliveAgentsSwingWorker extends SwingWorker<List<SecretAgent>, Void> {

        @Override
        protected List<SecretAgent> doInBackground() throws Exception {
            return agentManager.findAliveAgents();
        }

        @Override
        protected void done() {
            agentModel.removeAllAgents();
            try {
                for (SecretAgent agent : get()) {
                    agentModel.addAgent(agent);
                }

            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());

            }
        }
    }

    private class ListDeadAgentsSwingWorker extends SwingWorker<List<SecretAgent>, Void> {

        @Override
        protected List<SecretAgent> doInBackground() throws Exception {
            List<SecretAgent> all = agentManager.findAllAgents();
            List<SecretAgent> alive = agentManager.findAliveAgents();
            all.removeAll(alive);
            return all;
        }

        @Override
        protected void done() {
            agentModel.removeAllAgents();
            try {
                for (SecretAgent agent : get()) {
                    agentModel.addAgent(agent);
                }

            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());

            }
        }
    }
}
