package secret_service;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import other.DBUtils;
import other.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static secret_service.AgentManagerTest.newSecretAgent;
import static secret_service.MissionManagerImplTest.newMission;

public class SecretServiceImplTest {

    private SecretServiceImpl secretService;
    private MissionManagerImpl missionManager;
    private AgentManagerImpl agentManager;
    private DataSource ds;

    private Mission m1, m2, m3, missionWithNullId, missionNotInDB, missionIncomplete;
    private SecretAgent a1, a2, agentWithLowClearance, agentWithNullId, agentNotInDB;

    private void prepareTestData() {

        m1 = newMission("The cake is a lie", "Slovenia", 5, LocalDate.of(2008,1,1), "Investigate if the cake is a lie.");
        m2 = newMission("Coffee", "England", 1, LocalDate.of(2008, 1, 2), "Where is my coffee?");
        m3 = newMission("Assassination", "Korea", 5, LocalDate.of(2008,1,3), "I don't like her ex.");
        missionIncomplete = newMission("Assassination", "Korea", 5, null, "I don't like her ex.");
        a1 = newSecretAgent(null, "John", "male", LocalDate.of(1980, 2, 5), 6);
        a2 = newSecretAgent(null, "John3", "male", LocalDate.of(1999,3,6), 6);
        agentWithLowClearance = newSecretAgent(null, "John2", "male", LocalDate.of(1970, 2, 5), 1);

        agentManager.createAgent(a1);
        agentManager.createAgent(a2);
        agentManager.createAgent(agentWithLowClearance);

        missionManager.createMission(m1);
        missionManager.createMission(m2);
        missionManager.createMission(m3);
        missionManager.createMission(missionIncomplete);

        agentWithNullId = newSecretAgent(null, "John4", "male", LocalDate.of(1980, 2, 5), 6);
        agentNotInDB = newSecretAgent(null, "John5", "male", LocalDate.of(1981, 2, 5), 6);
        agentNotInDB.setId(a2.getId() + 100);
        missionWithNullId = newMission("Mission with null id", null, 1, LocalDate.of(1999,9,9), null);
        missionNotInDB = newMission("Mission not in DB", null, 1, LocalDate.of(1999, 9, 9), null);
        missionNotInDB.setId(m3.getId() + 100);

    }

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:agentmanager-test;create=true");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, SecretService.class.getResourceAsStream("/createTables.sql"));
        secretService = new SecretServiceImpl(ds);
        missionManager = new MissionManagerImpl(ds);
        agentManager = new AgentManagerImpl(ds);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, SecretService.class.getResourceAsStream("/dropTables.sql"));
    }

    @Test
    public void deletingAgentGlobally() throws Exception {
        secretService.assignAgentToMission(a1, m2);
        secretService.assignAgentToMission(a1,missionIncomplete);

        agentManager.deleteAgent(a1);
        assertNull(secretService.findAgentOnMission(missionIncomplete));
        assertNull(secretService.findAgentOnMission(m2));
    }

    @Test
    public void testAssignAgentToMission() throws Exception {

        secretService.assignAgentToMission(a2,m2);
        SecretAgent retrievedAgent = secretService.findAgentOnMission(m2);
        assertNotNull(retrievedAgent);
        assertTrue(retrievedAgent.equals(a2));

        secretService.assignAgentToMission(a1,missionIncomplete);
        retrievedAgent = secretService.findAgentOnMission(missionIncomplete);
        assertNotNull(retrievedAgent);
        assertTrue(retrievedAgent.equals(a1));

        try {
            //already on active mission
            secretService.assignAgentToMission(a1,m2);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }

        try {
            //already occupied with agent
            secretService.assignAgentToMission(a2,missionIncomplete);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }


        try {
            secretService.assignAgentToMission(agentWithLowClearance, m1);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }
    }

    @Test
    public void testRemoveAgentFromMission() throws Exception {
        secretService.assignAgentToMission(a1, m1);
        secretService.removeAgentFromMission(a1,m1);

        List<Mission> missions = secretService.findMissionsWithAgent(a1);
        assertFalse(missions.contains(m1));

        try {
            secretService.removeAgentFromMission(a2,m2);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }

        try {
            secretService.removeAgentFromMission(agentNotInDB,m1);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }

        try {
            secretService.removeAgentFromMission(a1,missionNotInDB);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }

        try {
            secretService.removeAgentFromMission(a1,missionWithNullId);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }
        try {
            secretService.removeAgentFromMission(null,null);
            fail();
        }
        catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void testFindMissionsWithAgent() throws Exception {
        assertTrue(secretService.findMissionsWithAgent(a1).isEmpty());

        secretService.assignAgentToMission(a1,m2);
        secretService.assignAgentToMission(a1, missionIncomplete);

        List<Mission> missions = secretService.findMissionsWithAgent(a1);

        assertTrue(missions.contains(missionIncomplete));
        assertTrue(missions.contains(m2));

        try {
            missions = secretService.findMissionsWithAgent(null);
            fail();
        }
        catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            missions = secretService.findMissionsWithAgent(agentWithNullId);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }

    }

    @Test
    public void testFindAgentOnMission() throws Exception {
        assertNull(secretService.findAgentOnMission(m1));

        secretService.assignAgentToMission(a1,m1);
        assertEquals(secretService.findAgentOnMission(m1), a1);

        secretService.removeAgentFromMission(a1,m1);
        assertNull(secretService.findAgentOnMission(m1));

        try {
            SecretAgent agent = secretService.findAgentOnMission(null);
            fail();
        }
        catch (IllegalArgumentException ex) {
            //OK
        }
        try {
            SecretAgent agent = secretService.findAgentOnMission(missionWithNullId);
            fail();
        }
        catch (ServiceFailureException ex) {
            //OK
        }
    }

}