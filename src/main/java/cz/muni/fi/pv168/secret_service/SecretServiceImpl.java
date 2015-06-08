package cz.muni.fi.pv168.secret_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import other.ServiceFailureException;

import static cz.muni.fi.pv168.secret_service.AgentManagerImpl.agentMapper;
import static cz.muni.fi.pv168.secret_service.MissionManagerImpl.missionMapper;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public class SecretServiceImpl implements SecretService {

    final static Logger log = LoggerFactory.getLogger(SecretServiceImpl.class);

    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    public SecretServiceImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Override
    public void assignAgentToMission(SecretAgent secretAgent, Mission mission) throws ServiceFailureException, IllegalArgumentException {
        log.debug("assignAgentToMission({},{})", secretAgent, mission);
        if (secretAgent == null) {
            log.error("agent is null({})", secretAgent);
            throw new IllegalArgumentException("agent is null");
        }
        if (secretAgent.getId() == null) {
            log.error("agent id is null({})", secretAgent);
            throw new ServiceFailureException("agent id is null");
        }
        if (mission == null) {
            log.error("mission is null({})", mission);
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            log.error("mission id is null({})", mission);
            throw new ServiceFailureException("mission id is null");
        }

        transaction.execute(transactionStatus -> {
            //first check if agent not already active
            int activeMissionsCount = jdbc.queryForObject(
                    "SELECT count(*) FROM Mission WHERE agent = ? AND completion IS NULL ", Integer.class, secretAgent.getId());

            if (activeMissionsCount != 0) {
                log.error("Agent already on active mission({})", secretAgent);
                throw new ServiceFailureException("Agent " + secretAgent + " already on active mission.");
            }

            //updating mission
            int n = jdbc.update("UPDATE Mission SET agent = ? WHERE id = ? AND reqClearance <= ? AND agent IS NULL", secretAgent.getId(), mission.getId(),secretAgent.getClearanceLevel());
            if (n == 0) {
                log.error("error assigning agent({}) to a mission ({})", secretAgent, mission);
                throw new ServiceFailureException("Mission " + mission + " not found or some agent already on mission or low clearance of agent.");
            }
            return null;
        });
    }

    @Override
    public void removeAgentFromMission(SecretAgent secretAgent, Mission mission) throws ServiceFailureException, IllegalArgumentException {
        log.debug("removeAgentFromMission({},{})", secretAgent, mission);
        if (secretAgent == null) {
            log.error("agent is null({})", secretAgent);
            throw new IllegalArgumentException("agent is null");
        }
        if (secretAgent.getId() == null) {
            log.error("agent id is null({})", secretAgent);
            throw new ServiceFailureException("agent id is null");
        }
        if (mission == null) {
            log.error("mission is null({})", mission);
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            log.error("mission id is null({})", mission);
            throw new ServiceFailureException("mission id is null");
        }
        int n = jdbc.update("UPDATE Mission SET agent = NULL WHERE ID = ? AND agent = ?", mission.getId(), secretAgent.getId());
        if(n!=1) {
            log.error("error removing agent({}) from a mission ({})", secretAgent, mission);
            throw new ServiceFailureException("agent id=" + secretAgent.getId() + " is not on mission id =" + mission.getId());
        }
    }


    @Override
    public List<Mission> findMissionsWithAgent(SecretAgent secretAgent) throws ServiceFailureException, IllegalArgumentException {
        log.debug("findMissionsWithAgent({})", secretAgent);
        if (secretAgent == null) {
            log.error("agent is null({})", secretAgent);
            throw new IllegalArgumentException("agent is null");
        }
        if (secretAgent.getId() == null) {
            log.error("agent id is null({})", secretAgent);
            throw new ServiceFailureException("agent id is null");
        }
        List<Mission> missions = jdbc.query(
                "SELECT mission.ID, title, country, description, completion, reqClearance FROM mission JOIN agent ON agent.ID = mission.agent " +
                        "WHERE agent.id = ?", missionMapper, secretAgent.getId());
        return missions;
    }

    @Override
    public SecretAgent findAgentOnMission(Mission mission) throws ServiceFailureException, IllegalArgumentException{
        log.debug("findAgentOnMission({})", mission);
        if (mission == null) {
            log.error("mission is null({})", mission);
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            log.error("mission id is null({})", mission);
            throw new ServiceFailureException("mission id is null");
        }
        List<SecretAgent> agents = jdbc.query(
                "SELECT agent.ID, name, gender, clearance, birth, death FROM agent JOIN mission ON agent.ID = mission.agent " +
                        "WHERE mission.id = ?", agentMapper, mission.getId());
        return agents.isEmpty() ? null : agents.get(0);
    }
}
