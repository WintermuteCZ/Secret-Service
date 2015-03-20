package secret_service;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.Assert.*;

public class AgentManagerTest {

    AgentManager agentManager;

    @Before
    public void setUp() throws Exception {
        agentManager = new AgentManagerImpl();
    }

    @Test
    public void RetrievableAgentTest() {
        SecretAgent agent = newSecretAgent(007L,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agentManager.createAgent(agent);

        Collection<SecretAgent> list =  agentManager.findAllAgents();
        assertTrue(list.contains(agent));

        long id = agent.getId();
        assertEquals(agent, agentManager.findAgentByID(id));
    }

    @Test
    public void FindAliveAgentTest() {
        SecretAgent agent = newSecretAgent(007L,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agent.setDateOfDeath(LocalDate.of(2012, 5, 3));
        agentManager.createAgent(agent);

        SecretAgent agent2 = newSecretAgent(123L,"John", "male", LocalDate.of(1963,5,6), 1);
        agentManager.createAgent(agent2);

        Collection<SecretAgent> list = agentManager.findAliveAgents();
        assertFalse(list.contains(agent));
        assertTrue(list.contains(agent2));
    }

    @Test
    public void DeleteAgentTest() {
        SecretAgent agent = newSecretAgent(007L,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agentManager.createAgent(agent);
        agentManager.deleteAgent(agent);
        assertFalse(agentManager.findAllAgents().contains(agent));
    }

    @Test
    public void DeleteAgentWrongAttributesTest() {
        SecretAgent agent = newSecretAgent(123L,"John", "male", LocalDate.of(1963,5,6), 1);
        agentManager.createAgent(agent);

        try {
            agentManager.deleteAgent(null);
            fail();
        }
        catch (IllegalArgumentException e) {
        }

        try {
            agent.setId(321L);
            agentManager.deleteAgent(agent);
            fail();
        }
        catch (IllegalArgumentException e) {
        }

    }

    @Test
    public void UpdateAgentTest() {
        SecretAgent agent = newSecretAgent(120L,"John", "male", LocalDate.of(1963,5,6), 1);
        agentManager.createAgent(agent);
        long id = agent.getId();

        agent = agentManager.findAgentByID(id);
        agent.setClearanceLevel(2);
        agentManager.updateAgent(agent);
        assertEquals("John", agent.getName());
        assertEquals("male", agent.getGender());
        assertEquals(LocalDate.of(1963,5,6), agent.getDateOfBirth());
        assertNull(agent.getDateOfDeath());
        assertEquals(2, agent.getClearanceLevel());

        agent = agentManager.findAgentByID(id);
        agent.setGender("female");
        agentManager.updateAgent(agent);
        assertEquals("John", agent.getName());
        assertEquals("female", agent.getGender());
        assertEquals(LocalDate.of(1963,5,6), agent.getDateOfBirth());
        assertNull(agent.getDateOfDeath());
        assertEquals(2, agent.getClearanceLevel());


        agent = agentManager.findAgentByID(id);
        agent.setDateOfBirth(LocalDate.of(1953, 5, 6));
        agentManager.updateAgent(agent);
        assertEquals("John", agent.getName());
        assertEquals("female", agent.getGender());
        assertEquals(LocalDate.of(1953,5,6), agent.getDateOfBirth());
        assertNull(agent.getDateOfDeath());
        assertEquals(2, agent.getClearanceLevel());


        agent = agentManager.findAgentByID(id);
        agent.setDateOfDeath(LocalDate.of(1993, 5, 6));
        agentManager.updateAgent(agent);
        assertEquals("John", agent.getName());
        assertEquals("female", agent.getGender());
        assertEquals(LocalDate.of(1953,5,6), agent.getDateOfBirth());
        assertNotNull(agent.getDateOfDeath());
        assertEquals(LocalDate.of(1993,5,6),agent.getDateOfDeath());
        assertEquals(2, agent.getClearanceLevel());


    }


    private static SecretAgent newSecretAgent(Long id, String name, String gender, LocalDate dateOfBirth, int clearanceLevel) {
        SecretAgent agent = new SecretAgent();
        agent.setId(id);
        agent.setName(name);
        agent.setGender(gender);
        agent.setDateOfBirth(dateOfBirth);
        agent.setClearanceLevel(clearanceLevel);
        return agent;
    }
}