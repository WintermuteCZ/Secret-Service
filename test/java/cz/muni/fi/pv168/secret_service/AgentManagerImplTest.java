package cz.muni.fi.pv168.secret_service;

import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import other.DBUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import other.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.Assert.*;
public class AgentManagerImplTest {

    private AgentManagerImpl agentManager;
    private DataSource ds;

    final static Logger log = LoggerFactory.getLogger(AgentManagerImplTest.class);

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agentmanager-test;create=true");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        log.debug("agent manager test setting up database");
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, AgentManager.class.getResourceAsStream("/createTables.sql"));
        agentManager = new AgentManagerImpl(ds);
    }

    @After
    public void tearDown() throws SQLException {
        log.debug("agent manager test dropping database");
        DBUtils.executeSqlScript(ds, AgentManager.class.getResourceAsStream("/dropTables.sql"));
    }


    @Test
    public void retrievableAgentTest() {
        log.info("testing retrieveable agent");
        SecretAgent agent = newSecretAgent(null,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agentManager.createAgent(agent);

        Collection<SecretAgent> list =  agentManager.findAllAgents();
        assertTrue(list.contains(agent));

        Long id = agent.getId();
        assertEquals(agent, agentManager.findAgentByID(id));
    }

    @Test
    public void findAllAgentTest() {
        log.info("testing finding all agents");
        SecretAgent agent = newSecretAgent(null,"Bond", "male", LocalDate.of(1953,5,6), 10);
        SecretAgent agent2 = newSecretAgent(null,"John", "male", LocalDate.of(1963,5,6), 1);
        SecretAgent agent3 = newSecretAgent(null,"John2", "male", LocalDate.of(1967,5,6), 1);

        agentManager.createAgent(agent);
        agentManager.createAgent(agent2);
        agentManager.createAgent(agent3);

        Collection<SecretAgent> agentList = agentManager.findAllAgents();
        assertTrue(agentList.contains(agent));
        assertTrue(agentList.contains(agent2));
        assertTrue(agentList.contains(agent3));

    }

    @Test
    public void findAliveAgentTest() {
        log.info("testing finding alive agent");
        SecretAgent agent = newSecretAgent(null,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agent.setDateOfDeath(LocalDate.of(2012, 5, 3));
        agentManager.createAgent(agent);

        SecretAgent agent2 = newSecretAgent(null,"John", "male", LocalDate.of(1963,5,6), 1);
        agentManager.createAgent(agent2);

        Collection<SecretAgent> list = agentManager.findAliveAgents();
        assertFalse(list.contains(agent));
        assertTrue(list.contains(agent2));
    }

    @Test
    public void deleteAgentTest() {
        log.info("testing deleting agent");
        SecretAgent agent = newSecretAgent(null,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agentManager.createAgent(agent);
        agentManager.deleteAgent(agent);
        assertFalse(agentManager.findAllAgents().contains(agent));
    }

    @Test
    public void deleteAgentWrongAttributesTest() {
        log.info("testing deleting agent with wrong attributes");
        SecretAgent agent = newSecretAgent(null,"John", "male", LocalDate.of(1963,5,6), 1);

        agentManager.createAgent(agent);

        try {
            agentManager.deleteAgent(null);
            fail();
        }
        catch (IllegalArgumentException e) {
            //OK
        }

        try {
            agent.setId(321L);
            agentManager.deleteAgent(agent);
            fail();
        }
        catch (ServiceFailureException e) {
            //OK
        }

        try {
            agent.setId(null);
            agentManager.deleteAgent(agent);
            fail();
        }
        catch (IllegalArgumentException e) {
            //OK
        }

        try {
            agent.setId(-1L);
            agentManager.deleteAgent(agent);
            fail();
        }
        catch (ServiceFailureException e) {
            //OK
        }

    }

    @Test
    public void updateAgentTest() {
        log.info("testing updating agent");
        SecretAgent agent = newSecretAgent(null,"John", "male", LocalDate.of(1963,5,6), 1);
        agentManager.createAgent(agent);
        Long id = agent.getId();

        agent.setClearanceLevel(2);
        agentManager.updateAgent(agent);
        agent = agentManager.findAgentByID(id);
        assertEquals(2, agent.getClearanceLevel());

        agent.setGender("female");
        agentManager.updateAgent(agent);
        agent = agentManager.findAgentByID(id);
        assertEquals("female", agent.getGender());


        agent.setDateOfBirth(LocalDate.of(1953, 5, 6));
        agentManager.updateAgent(agent);
        agent = agentManager.findAgentByID(id);
        assertEquals(LocalDate.of(1953,5,6), agent.getDateOfBirth());


        agent.setDateOfDeath(LocalDate.of(1993, 5, 6));
        agentManager.updateAgent(agent);
        agent = agentManager.findAgentByID(agent.getId());
        assertEquals(LocalDate.of(1993,5,6),agent.getDateOfDeath());


    }


    static SecretAgent newSecretAgent(Long id, String name, String gender, LocalDate dateOfBirth, int clearanceLevel) {
        SecretAgent agent = new SecretAgent();
        agent.setId(id);
        agent.setName(name);
        agent.setGender(gender);
        agent.setDateOfBirth(dateOfBirth);
        agent.setClearanceLevel(clearanceLevel);
        return agent;
    }
}