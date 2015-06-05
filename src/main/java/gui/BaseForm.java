package gui;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import other.DBUtils;
import secret_service.AgentManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Martina on 3.6.2015.
 */
public class BaseForm {

    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private static Logger log = LoggerFactory.getLogger(BaseForm.class);
    private static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());

    public BaseForm() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agencydb-test;create=true");

        DBUtils.executeSqlScript(ds, AgentManager.class.getResourceAsStream("/createTables.sql"));
        Locale.setDefault(new Locale("cs", "CZ"));
        tabbedPane1.add(bundle.getString("AgentManagement"), new AgentPanel().getPanel1());
        tabbedPane1.add(bundle.getString("MissionManagement"), new MissionPanel().getPanel1());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.debug("BaseForm start({})");
                JFrame frame = new JFrame("SecretService");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                try {
                    frame.setContentPane(new BaseForm().tabbedPane1);
                } catch (SQLException e){
                    e.printStackTrace();
                }

                frame.setPreferredSize(new Dimension(800, 600));
                //frame.setJMenuBar(createMenu());

                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
