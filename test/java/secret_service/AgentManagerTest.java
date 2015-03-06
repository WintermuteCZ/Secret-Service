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
        SecretAgent agent = new SecretAgent();
        agent.setId(1);
        agentManager.createAgent(agent);
        Collection<SecretAgent> list =  agentManager.findAllAgents();
        assertTrue(list.contains(agent));
    }

    @Test
    public void FindAliveAgentTest() {
        SecretAgent agent = new SecretAgent();
        agent.setId(1);
        agent.setDateOfBirth(LocalDate.of(1973,3,6));
        agent.setDateOfDeath(LocalDate.of(2014, 1, 2));
        agentManager.createAgent(agent);

        SecretAgent agent2 = new SecretAgent();
        agent.setId(2);
        agent.setDateOfBirth(LocalDate.of(1981, 3, 6));
        agentManager.createAgent(agent2);

        Collection<SecretAgent> list = agentManager.findAliveAgents();
        assertFalse(list.contains(agent));
        assertTrue(list.contains(agent2));
    }

}