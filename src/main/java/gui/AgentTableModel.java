package gui;

import secret_service.SecretAgent;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Martina on 3.6.2015.
 */
public class AgentTableModel extends AbstractTableModel {
    private List<SecretAgent> agents = new ArrayList<>();
    private static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());
    @Override
    public int getRowCount() {
        return agents.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SecretAgent agent = agents.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return agent.getId();
            case 1:
                return agent.getName();
            case 2:
                return agent.getGender();
            case 3:
                return agent.getDateOfBirth();
            case 4:
                return agent.getDateOfDeath();
            case 5:
                return agent.getClearanceLevel();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
            case 2:
                return String.class;
            case 3:
            case 4:
                return LocalDate.class;
            case 5:
                return int.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addAgent(SecretAgent agent) {
        agents.add(agent);
        int lastRow = agents.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public SecretAgent getAgent(int rowIndex){
        SecretAgent agent = agents.get(rowIndex);
        return agent;
    }

    public void removeAgent(SecretAgent agent) {
        agents.remove(agent);
        int lastRow = agents.size() - 1;
        fireTableRowsDeleted(lastRow, lastRow);
    }

    public void removeAllAgents(){
        agents.clear();
        fireTableDataChanged();
    }

    void updateAgent(SecretAgent agent) {
        int i;
        for (i = 0; i < agents.size() - 1; i++) {
            if (agents.get(i).getId() == agent.getId()) {
                break;
            }
        }
        agents.set(i, agent);
        fireTableRowsUpdated(i, i);
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return bundle.getString("Id");
            case 1:
                return bundle.getString("Name");
            case 2:
                return bundle.getString("Gender");
            case 3:
                return bundle.getString("DateOfBirth");
            case 4:
                return bundle.getString("DateOfDeath");
            case 5:
                return bundle.getString("Level");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SecretAgent agent = agents.get(rowIndex);
        switch (columnIndex) {
            case 0:
                agent.setId((Long) aValue);
                break;
            case 1:
                agent.setName((String) aValue);
                break;
            case 2:
                agent.setGender((String) aValue);
                break;
            case 3:
                agent.setDateOfBirth((LocalDate) aValue);
                break;
            case 4:
                agent.setDateOfDeath((LocalDate) aValue);
                break;
            case 5:
                agent.setClearanceLevel((int) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
