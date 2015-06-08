package cz.muni.fi.pv168.secret_service;

import java.util.List;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public interface MissionManager {
    void createMission(Mission mission);

    void updateMission(Mission mission);

    void deleteMission(Mission mission);

    List<Mission> findAllMissions();

    List<Mission> findAvailableMissions();

    List<Mission> findCompletedMissions();

    Mission findMissionByID(Long id);
}
