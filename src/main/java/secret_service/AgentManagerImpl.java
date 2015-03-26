package secret_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public class AgentManagerImpl implements AgentManager {

    final static Logger log = LoggerFactory.getLogger(SecretServiceImpl.class);

    private final DataSource dataSource;

    public AgentManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createAgent(SecretAgent secretAgent) {
        if (secretAgent == null) {
            throw new IllegalArgumentException("secretAgent is null");
        }
        if (secretAgent.getId() != null) {
            throw new IllegalArgumentException("agent id is already set");
        }
        if (secretAgent.getName()== null) {
            throw new IllegalArgumentException("agent name is null");
        }
        if (secretAgent.getGender() == null) {
            throw new IllegalArgumentException("agent gender is null");
        }
        if (secretAgent.getClearanceLevel() < 0) {
            throw new IllegalArgumentException("clearance level is negative number");
        }
        if (secretAgent.getDateOfBirth() == null) {
            throw new IllegalArgumentException("date of birth is null");
        }
        if (secretAgent.getDateOfDeath().isBefore(secretAgent.getDateOfBirth())) {
            throw new IllegalArgumentException("date of death is before date of birth");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO AGENT (name,gender,clearance,birth,death) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, secretAgent.getName());
                st.setString(2, secretAgent.getGender());
                st.setInt(3, secretAgent.getClearanceLevel());
                LocalDate birth = secretAgent.getDateOfBirth();
                st.setObject(4, birth == null ? null : birth.toString(), Types.DATE);
                LocalDate death = secretAgent.getDateOfDeath();
                st.setObject(5, death == null ? null : death.toString(), Types.DATE);
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert agent " + secretAgent);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                secretAgent.setId(getKey(keyRS, secretAgent));
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all agents", ex);
        }
    }


    private Long getKey(ResultSet keyRS, SecretAgent secretAgent) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert agent " + secretAgent
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert agent " + secretAgent
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert agent " + secretAgent
                    + " - no key found");
        }
    }

    @Override
    public void updateAgent(SecretAgent secretAgent) {
        if (secretAgent == null) {
            throw new IllegalArgumentException("secretAgent is null");
        }
        if (secretAgent.getId() == null) {
            throw new IllegalArgumentException("agent with null id cannot be updated");
        }
        if (secretAgent.getName()== null) {
            throw new IllegalArgumentException("agent name is null");
        }
        if (secretAgent.getGender() == null) {
            throw new IllegalArgumentException("agent gender is null");
        }
        if (secretAgent.getClearanceLevel() < 0) {
            throw new IllegalArgumentException("clearance level is negative number");
        }
        if (secretAgent.getDateOfBirth() == null) {
            throw new IllegalArgumentException("date of birth is null");
        }
        if (secretAgent.getDateOfDeath().isBefore(secretAgent.getDateOfBirth())) {
            throw new IllegalArgumentException("date of death is before date of birth");
        }

        try (Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement("UPDATE agent SET name=?,gender=?,clearance=?,birth=?,death=? WHERE id=?")) {
                st.setString(1, secretAgent.getName());
                st.setString(2, secretAgent.getGender());
                st.setInt(3, secretAgent.getClearanceLevel());
                LocalDate birth = secretAgent.getDateOfBirth();
                st.setObject(4, birth == null ? null : birth.toString(), Types.DATE);
                LocalDate death = secretAgent.getDateOfDeath();
                st.setObject(5, death == null ? null : death.toString(), Types.DATE);
                st.setLong(6,secretAgent.getId());
                if(st.executeUpdate()!=1) {
                    throw new IllegalArgumentException("cannot update agent " + secretAgent);
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all agents", ex);
        }
    }

    @Override
    public void deleteAgent(SecretAgent secretAgent) {
        if (secretAgent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (secretAgent.getId() == null) {
            throw new IllegalArgumentException("agent with null id cannot be deleted");
        }
        try (Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement("DELETE FROM grave WHERE id=?")) {
                st.setLong(1,secretAgent.getId());
                if(st.executeUpdate()!=1) {
                    throw new ServiceFailureException("did not delete agent with id ="+secretAgent.getId());
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all agents", ex);
        }
    }

    @Override
    public List<SecretAgent> findAllAgents() {
        return null;
    }

    @Override
    public List<SecretAgent> findAliveAgents() {
        return null;
    }

    @Override
    public SecretAgent findAgentByID(Long id) {
        return null;
    }
}
