package secret_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import other.ServiceFailureException;
import other.ValidationException;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 *
 */
public class AgentManagerImpl implements AgentManager {

    final static Logger log = LoggerFactory.getLogger(AgentManagerImpl.class);

    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;

    public AgentManagerImpl(DataSource dataSource) {

        this.jdbc = new JdbcTemplate(dataSource);
        this.transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    //result set -> instance
    public static final RowMapper<SecretAgent> agentMapper = (rs, rowNum) -> {
        String birth = rs.getString("birth");
        String death = rs.getString("death");
        return new SecretAgent(
                rs.getLong("ID"),
                rs.getString("name"),
                rs.getString("gender"),
                birth == null ? null : LocalDate.parse(birth),
                death == null ? null : LocalDate.parse(death),
                rs.getInt("clearance"));
    };

    static private void validate(SecretAgent secretAgent) {
        log.debug("validate ({})", secretAgent);
        if (secretAgent == null) {
            log.error("agent is null({})", secretAgent);
            throw new IllegalArgumentException("agent is null");
        }
        if (secretAgent.getName() == null) {
            log.error("name is null({})", secretAgent);
            throw new ValidationException("name is null");
        }
        if (secretAgent.getGender() == null) {
            log.error("gender is null({})", secretAgent);
            throw new ValidationException("gender is null");
        }
        if (secretAgent.getClearanceLevel() < 0) {
            log.error("clearance level is negative number({})", secretAgent);
            throw new ValidationException("clearance level is negative number");
        }
        if (secretAgent.getDateOfBirth() == null) {
            log.error("date of birth is null({})", secretAgent);
            throw new ValidationException("date of birth is null");
        }
        if (secretAgent.getDateOfDeath() != null) {
            if (secretAgent.getDateOfDeath().isBefore(secretAgent.getDateOfBirth())) {
                log.error("date of death is before date of birth({})", secretAgent);
                throw new ValidationException("date of death is before date of birth");
            }
        }
    }

    @Override
    public void createAgent(SecretAgent secretAgent) {
        log.debug("createAgent({})", secretAgent);
        validate(secretAgent);
        if (secretAgent.getId() != null) {
            log.error("agent id already set({})", secretAgent);
            throw new ServiceFailureException("agent id already set");
        }

        Map<String, Object> pars = new HashMap<>();
        pars.put("name", secretAgent.getName());
        pars.put("gender", secretAgent.getGender());
        pars.put("birth", secretAgent.getDateOfBirth() == null ? null : secretAgent.getDateOfBirth().toString());
        pars.put("death", secretAgent.getDateOfDeath() == null ? null : secretAgent.getDateOfDeath().toString());
        pars.put("clearance", secretAgent.getClearanceLevel());

        Long id = new SimpleJdbcInsert(jdbc).withTableName("agent").usingGeneratedKeyColumns("ID").executeAndReturnKey(pars).longValue();
        secretAgent.setId(id);
    }

    @Override
    public void updateAgent(SecretAgent secretAgent) {
        log.debug("updateAgent({})", secretAgent);
        validate(secretAgent);
        if (secretAgent.getId() == null) {
            log.error("agent id is null({})", secretAgent);
            throw new IllegalArgumentException("agent id is null");
        }
        if (findAgentByID(secretAgent.getId()) != null) {
            if (findAgentByID(secretAgent.getId()).getDateOfDeath() != null && secretAgent.getDateOfDeath() == null) {
                log.error("valid date of death changed to null({})", secretAgent);
                throw new ServiceFailureException("we don't have zombie agents (yet)");
            }
        }
        int n = jdbc.update("UPDATE agent SET name = ?, gender = ?, birth = ?, death = ?, clearance = ? WHERE ID = ?",
                secretAgent.getName(), secretAgent.getGender(),
                        secretAgent.getDateOfBirth() == null ? null : secretAgent.getDateOfBirth().toString(),
                        secretAgent.getDateOfDeath() == null ? null : secretAgent.getDateOfDeath().toString(),
                        secretAgent.getClearanceLevel(), secretAgent.getId());
        if(n!=1) {
            log.error("error updating agent({})", secretAgent);
            throw new ServiceFailureException("agent " + secretAgent + " not updated");
        }
    }

    @Override
    public void deleteAgent(SecretAgent secretAgent) throws ServiceFailureException, IllegalArgumentException {
        log.debug("deleteAgent({})", secretAgent);
        if (secretAgent == null) {
            log.error("agent is null({})", secretAgent);
            throw new IllegalArgumentException("agent is null");
        }
        if (secretAgent.getId() == null) {
            log.error("agent id is null({})", secretAgent);
            throw new IllegalArgumentException("agent id is null");
        }
        transaction.execute(transactionStatus -> {
            jdbc.update("UPDATE mission SET agent = NULL WHERE agent = ?", secretAgent.getId());

            int n = jdbc.update("DELETE FROM agent WHERE ID = ?", secretAgent.getId());
            if (n != 1) {
                log.error("error deleting agent({})", secretAgent);
                throw new ServiceFailureException("agent " + secretAgent + " not deleted");
            }
            return null;
        });
    }

    @Override
    public List<SecretAgent> findAllAgents() throws ServiceFailureException {
        log.debug("findAllAgents()");
        return jdbc.query("SELECT ID, name, gender, birth, death, clearance FROM agent", agentMapper);
    }

    @Override
    public List<SecretAgent> findAliveAgents() throws ServiceFailureException {
        log.debug("findAliveAgents()");
        return jdbc.query("SELECT ID, name, gender, birth, death, clearance FROM agent WHERE death IS NULL", agentMapper);
    }

    @Override
    public SecretAgent findAgentByID(Long id) throws ServiceFailureException, IllegalArgumentException {
        log.debug("findAgent({})", id);
        if (id == null) {
            log.error("id is null");
            throw new IllegalArgumentException("id is null");
        }
        List<SecretAgent> list = jdbc.query("SELECT ID, name, gender, birth, death, clearance FROM agent WHERE id = ?", agentMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }
}
