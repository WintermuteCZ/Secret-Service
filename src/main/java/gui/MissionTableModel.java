package gui;

import secret_service.Mission;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Martina on 3.6.2015.
 */
public class MissionTableModel extends AbstractTableModel {
    private List<Mission> missions = new ArrayList<>();
    private static ResourceBundle bundle = ResourceBundle.getBundle("localization", Locale.getDefault());

    @Override
    public int getRowCount() {
        return missions.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Mission mission = missions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return mission.getId();
            case 1:
                return mission.getTitle();
            case 2:
                return mission.getCountry();
            case 3:
                return mission.getDescription();
            case 4:
                return mission.getDateOfCompletion();
            case 5:
                return mission.getRequiredClearance();
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
            case 3:
                return String.class;
            case 4:
                return LocalDate.class;
            case 5:
                return int.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addMission(Mission mission) {
        missions.add(mission);
        int lastRow = missions.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public Mission getMission(int rowIndex) {
        Mission mission = missions.get(rowIndex);
        return mission;
    }

    public void removeMission(Mission mission) {
        missions.remove(mission);
        int lastRow = missions.size() - 1;
        fireTableRowsDeleted(lastRow, lastRow);
    }

    public void removeAllMissions() {
        missions.clear();
        fireTableDataChanged();
    }


    void updateMission(Mission mission) {
        int i;
        for (i = 0; i < missions.size() - 1; i++) {
            if (missions.get(i).getId() == mission.getId()) {
                break;
            }
        }
        missions.set(i, mission);
        fireTableRowsUpdated(i, i);
    }
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return bundle.getString("Id");
            case 1:
                return bundle.getString("Title");
            case 2:
                return bundle.getString("Country");
            case 3:
                return bundle.getString("Description");
            case 4:
                return bundle.getString("DateOfCompletion");
            case 5:
                return bundle.getString("RequiredClearance");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Mission mission = missions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                mission.setId((Long) aValue);
                break;
            case 1:
                mission.setTitle((String) aValue);
                break;
            case 2:
                mission.setCountry((String) aValue);
                break;
            case 3:
                mission.setDescription((String) aValue);
                break;
            case 4:
                mission.setDateOfCompletion((LocalDate) aValue);
                break;
            case 5:
                mission.setRequiredClearance((int) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
