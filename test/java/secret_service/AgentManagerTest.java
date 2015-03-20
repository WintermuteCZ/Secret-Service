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
    public void retrievableAgentTest() {
        SecretAgent agent = newSecretAgent(null,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agentManager.createAgent(agent);

        Collection<SecretAgent> list =  agentManager.findAllAgents();
        assertTrue(list.contains(agent));

        Long id = agent.getId();
        assertEquals(agent, agentManager.findAgentByID(id));
    }

    @Test
    public void findAliveAgentTest() {
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
        SecretAgent agent = newSecretAgent(null,"Bond", "male", LocalDate.of(1953,5,6), 10);
        agentManager.createAgent(agent);
        agentManager.deleteAgent(agent);
        assertFalse(agentManager.findAllAgents().contains(agent));
    }

    @Test
    public void deleteAgentWrongAttributesTest() {
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
        catch (IllegalArgumentException e) {
            //OK
        }

    }

    @Test
    public void updateAgentTest() {
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
        agent = agentManager.findAgentByID(id);
        assertEquals(LocalDate.of(1993,5,6),agent.getDateOfDeath());


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