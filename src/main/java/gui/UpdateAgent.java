package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import secret_service.AgentManager;
import secret_service.AgentManagerImpl;
import secret_service.SecretAgent;

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
public class UpdateAgent {
    private JComboBox agentLevelCombo;
    private JComboBox agentDeathYearCombo;
    private JTextField agentGenderTextField;
    private JTextField agentNameTextField;
    private JButton addAgentButton;
    private JComboBox agentBirthYearCombo;
    private JComboBox agentBirthMonthCombo;
    private JComboBox agentDeathDayCombo;
    private JComboBox agentDeathMonthCombo;
    private JComboBox agentBirthDayCombo;
    private JCheckBox agentDeathCheckBox;
    private JPanel panel1;

    public JPanel getPanel1() {
        return panel1;
    }

    private AgentManager manager;
    public static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());
    private static Logger log = LoggerFactory.getLogger(BaseForm.class);
    private AgentTableModel model;

    public UpdateAgent(SecretAgent agent, AgentTableModel model, JFrame iFrame) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");
        this.model = model;
        manager = new AgentManagerImpl(ds);

        agentNameTextField.setText(agent.getName());
        agentGenderTextField.setText(agent.getGender());

        agentLevelCombo.setSelectedItem(Integer.toString(agent.getClearanceLevel()));

        if (agent.getDateOfDeath() == null) {
            agentDeathCheckBox.setSelected(false);
        }
        else {
            agentDeathCheckBox.setSelected(true);
            agentDeathDayCombo.setSelectedItem(Integer.toString(agent.getDateOfDeath().getDayOfMonth()));
            agentDeathMonthCombo.setSelectedItem(Integer.toString(agent.getDateOfDeath().getMonthValue()));
            agentDeathYearCombo.setSelectedItem(Integer.toString(agent.getDateOfDeath().getYear()));
        }
        addAgentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int birthDay = Integer.parseInt(agentBirthDayCombo.getSelectedItem().toString());
                int birthMonth = Integer.parseInt(agentBirthMonthCombo.getSelectedItem().toString());
                int birthYear = Integer.parseInt(agentBirthYearCombo.getSelectedItem().toString());
                int deathDay = Integer.parseInt(agentDeathDayCombo.getSelectedItem().toString());
                int deathMonth = Integer.parseInt(agentDeathMonthCombo.getSelectedItem().toString());
                int deathYear = Integer.parseInt(agentDeathYearCombo.getSelectedItem().toString());
                boolean isDead = agentDeathCheckBox.isSelected();

                LocalDate birth = null;
                LocalDate death = null;
                try {
                    birth = LocalDate.of(birthYear, birthMonth, birthDay);
                    death = isDead ? LocalDate.of(deathYear, deathMonth, deathDay) : null;
                    if (death != null && birth.isAfter(death))
                    {
                        JFrame frame = new JFrame();
                        JOptionPane.showMessageDialog(frame,bundle.getString("DeathAfterBirth"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (DateTimeException e1) {
                    e1.printStackTrace();
                }

                if (agentNameTextField.getText().equals("") || agentGenderTextField.getText().equals("")) {
                    JFrame frame3 = new JFrame();
                    JOptionPane.showMessageDialog(frame3,bundle.getString("ErrorAgentNotFilled"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFrame frame2 = new JFrame();
                Object[] options = {bundle.getString("Yes"),
                        bundle.getString("No")};
                int n = JOptionPane.showOptionDialog(frame2,
                        bundle.getString("UpdateAgentQuestion") + " " + agent.getName(),
                        bundle.getString("UpdateAgent"),
                        JOptionPane.YES_OPTION,
                        JOptionPane.NO_OPTION,
                        null,
                        options,
                        options[1]);
                if (n != JOptionPane.YES_OPTION) {
                    return;
                }

                agent.setName(agentNameTextField.getText());
                agent.setGender(agentGenderTextField.getText());
                agent.setClearanceLevel(Integer.parseInt(agentLevelCombo.getSelectedItem().toString()));
                agent.setDateOfBirth(birth);
                agent.setDateOfDeath(death);

                UpdateAgentSwingWorker updateAgentSwingWorker = new UpdateAgentSwingWorker(agent);
                updateAgentSwingWorker.execute();

                iFrame.dispose();
            }
        });
    }

    private class UpdateAgentSwingWorker extends SwingWorker<Boolean, Void> {

        private SecretAgent agent;

        public UpdateAgentSwingWorker(SecretAgent agent) {
            this.agent = agent;
        }

        @Override
        protected Boolean doInBackground() {
            try {
                manager.updateAgent(agent);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel1, bundle.getString("ErrorUpdateAgent"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }

        protected void done() {
            try {
                if (get()) {
                    model.updateAgent(agent);
                }
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getMessage());
            }
        }
    }
}
