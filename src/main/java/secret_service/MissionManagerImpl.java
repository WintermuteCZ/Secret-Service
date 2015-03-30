package secret_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import other.ServiceFailureException;
import other.ValidationException;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public class MissionManagerImpl implements MissionManager {

    final static Logger log = LoggerFactory.getLogger(MissionManagerImpl.class);

    private final JdbcTemplate jdbc;

    public MissionManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    //result set -> instance
    public static final RowMapper<Mission> missionMapper = (rs, rowNum) -> {
        String completion = rs.getString("completion");
        return new Mission(
                rs.getLong("ID"),
                rs.getString("title"),
                rs.getString("country"),
                rs.getString("description"),
                completion == null ? null : LocalDate.parse(completion),
                rs.getInt("reqClearance"));
    };

    static private void validate(Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getTitle() == null) {
            throw new ValidationException("title is null");
        }
        if (mission.getCountry() == null) {
            throw new ValidationException("country is null");
        }
        if (mission.getRequiredClearance() < 0) {
            throw new ValidationException("required clearance is too low");
        }
    }

    @Override
    public void createMission(Mission mission) throws ServiceFailureException, ValidationException {
        log.debug("createMission({})", mission);
        validate(mission);
        if (mission.getId() != null) {
            throw new ServiceFailureException("mission id already set");
        }

        Map<String, Object> pars = new HashMap<>();
        pars.put("title", mission.getTitle());
        pars.put("country", mission.getCountry());
        pars.put("description", mission.getDescription());
        pars.put("completion", mission.getDateOfCompletion() == null ? null : mission.getDateOfCompletion().toString());
        pars.put("reqClearance", mission.getRequiredClearance());

        Long id = new SimpleJdbcInsert(jdbc).withTableName("mission").usingGeneratedKeyColumns("ID").executeAndReturnKey(pars).longValue();
        mission.setId(id);
    }

    @Override
    public void updateMission(Mission mission) throws ServiceFailureException, ValidationException, IllegalArgumentException {
        log.debug("updateMission({})", mission);
        validate(mission);
        if (mission.getId() == null) {
            throw new IllegalArgumentException("grave id is null");
        }
        if (findMissionByID(mission.getId()) != null) {
            if (findMissionByID(mission.getId()).getDateOfCompletion() != null && mission.getDateOfCompletion() == null) {
                throw new ServiceFailureException("missions can't be undone");
            }
        }
        int n = jdbc.update("UPDATE mission SET title = ?, country = ?, description = ?, completion = ?, reqClearance = ? WHERE ID = ?",
                mission.getTitle(), mission.getCountry(), mission.getDescription(),
                mission.getDateOfCompletion() == null ? null : mission.getDateOfCompletion().toString(),
                mission.getRequiredClearance(), mission.getId());
        if(n!=1) {
            throw new ServiceFailureException("mission " + mission + " not updated");
        }
    }

    @Override
    public void deleteMission(Mission mission) throws ServiceFailureException, IllegalArgumentException {
        log.debug("deleteMission({})", mission);
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalArgumentException("mission id is null");
        }
        int n = jdbc.update("DELETE FROM mission WHERE ID = ?", mission.getId());
        if(n!=1) {
            throw new ServiceFailureException("mission " + mission + " not deleted");
        }
    }

    @Override
    public List<Mission> findAllMissions() throws ServiceFailureException {
        log.debug("findAllMissions()");
        return jdbc.query("SELECT ID, title, country, description, completion, reqClearance FROM mission", missionMapper);
    }

    @Override
    public List<Mission> findAvailableMissions() throws ServiceFailureException {
        log.debug("findAvailableMissions()");
        return jdbc.query("SELECT ID, title, country, description, completion, reqClearance FROM mission WHERE completion IS NULL", missionMapper);
    }

    @Override
    public List<Mission> findCompletedMissions() throws ServiceFailureException {
        log.debug("findCompletedMissions()");
        return jdbc.query("SELECT ID, title, country, description, completion, reqClearance FROM mission WHERE completion IS NOT NULL", missionMapper);
    }

    @Override
    public Mission findMissionByID(Long id) throws ServiceFailureException, IllegalArgumentException {
        log.debug("findMission({})", id);
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        List<Mission> list = jdbc.query("SELECT ID, title, country, description, completion, reqClearance FROM mission WHERE ID = ?", missionMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }
}
