package secret_service;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class MissionManagerImplTest {

    MissionManager missionManager;

    @Before
    public void setUp() throws Exception {
        missionManager = new MissionManagerImpl();
    }

    @Test
    public void testRetrieveMission() throws Exception {
        Mission mission = newMission("Assassination", "Oz", 5, null, null);
        missionManager.createMission(mission);

        Long id = mission.getId();
        assertThat(mission, equalTo(missionManager.findMissionByID(id)));

        Collection<Mission> list =  missionManager.findAllMissions();
        assertThat(list, hasItem(mission));

        assertThat(mission, not(sameInstance(missionManager.findMissionByID(id))));
    }

    @Test
    public void testUpdateMission() throws Exception {
        Mission mission = newMission("Assassination", "Oz", 5, null, "Kill the wizard as violently as possible");
        missionManager.createMission(mission);

        Long id = mission.getId();

        mission.setTitle("Poisoning");
        missionManager.updateMission(mission);
        mission = missionManager.findMissionByID(id);
        assertThat("Poisoning", equalTo(mission.getTitle()));
        assertThat("Oz", equalTo(mission.getCountry()));
        assertThat(5, equalTo(mission.getRequiredClearance()));
        assertThat(mission.getDateOfCompletion(), nullValue());
        assertThat("Kill the wizard as violently as possible", equalTo(mission.getDescription()));

        mission.setCountry("Hungary");
        missionManager.updateMission(mission);
        mission = missionManager.findMissionByID(id);
        assertThat("Poisoning", equalTo(mission.getTitle()));
        assertThat("Hungary", equalTo(mission.getCountry()));
        assertThat(5, equalTo(mission.getRequiredClearance()));
        assertThat(mission.getDateOfCompletion(), nullValue());
        assertThat("Kill the wizard as violently as possible", equalTo(mission.getDescription()));

        mission.setRequiredClearance(9001);
        missionManager.updateMission(mission);
        mission = missionManager.findMissionByID(id);
        assertThat("Poisoning", equalTo(mission.getTitle()));
        assertThat("Hungary", equalTo(mission.getCountry()));
        assertThat(9001, equalTo(mission.getRequiredClearance()));
        assertThat(mission.getDateOfCompletion(), nullValue());
        assertThat("Kill the wizard as violently as possible", equalTo(mission.getDescription()));

        mission.setDateOfCompletion(LocalDate.of(2000,1,1));
        missionManager.updateMission(mission);
        mission = missionManager.findMissionByID(id);
        assertThat("Poisoning", equalTo(mission.getTitle()));
        assertThat("Hungary", equalTo(mission.getCountry()));
        assertThat(9001, equalTo(mission.getRequiredClearance()));
        assertThat(LocalDate.of(2000,1,1), equalTo(mission.getDateOfCompletion()));
        assertThat("Kill the wizard as violently as possible", equalTo(mission.getDescription()));

        mission.setTitle("Just don't make a lot of mess");
        missionManager.updateMission(mission);
        mission = missionManager.findMissionByID(id);
        assertThat("Poisoning", equalTo(mission.getTitle()));
        assertThat("Hungary", equalTo(mission.getCountry()));
        assertThat(9001, equalTo(mission.getRequiredClearance()));
        assertThat(LocalDate.of(2000,1,1), equalTo(mission.getDateOfCompletion()));
        assertThat("Just don't make a lot of mess", equalTo(mission.getDescription()));

        mission.setTitle(null);
        missionManager.updateMission(mission);
        mission = missionManager.findMissionByID(id);
        assertThat("Poisoning", equalTo(mission.getTitle()));
        assertThat("Hungary", equalTo(mission.getCountry()));
        assertThat(9001, equalTo(mission.getRequiredClearance()));
        assertThat(LocalDate.of(2000,1,1), equalTo(mission.getDateOfCompletion()));
        assertThat(mission.getDescription(), nullValue());
    }

    @Test
    public void testUpdateMissionWrongAttributes() throws Exception {
        Mission mission = newMission("Assassination", "Oz", 5, LocalDate.of(2000,1,1), "Kill the wizard as violently as possible");
        missionManager.createMission(mission);

        Long id = mission.getId();

        try {
            missionManager.updateMission(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission = missionManager.findMissionByID(id);
            mission.setId(null);
            missionManager.updateMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission = missionManager.findMissionByID(id);
            mission.setId(id - 1);
            missionManager.updateMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission = missionManager.findMissionByID(id);
            mission.setTitle(null);
            missionManager.updateMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission = missionManager.findMissionByID(id);
            mission.setCountry(null);
            missionManager.updateMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission = missionManager.findMissionByID(id);
            mission.setRequiredClearance(-1);
            missionManager.updateMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission = missionManager.findMissionByID(id);
            mission.setDateOfCompletion(null); //completed mission can't be undone
            missionManager.updateMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }
    }

    @Test
    public void testDeleteMission() throws Exception {
        Mission mission1 = newMission("Assassination", "Oz", 5, null, null);
        missionManager.createMission(mission1);

        Mission mission2 = newMission("Poisoning", "Hungary", 5, null, null);
        missionManager.createMission(mission2);

        missionManager.deleteMission(mission1);
        assertThat(missionManager.findAllMissions(), not(contains(mission1)));
        assertThat(missionManager.findAllMissions(), contains(mission2));
    }

    @Test
    public void testDeleteMissionWrongAttributes() throws Exception {
        Mission mission = newMission("Assassination", "Oz", 5, null, null);
        missionManager.createMission(mission);

        try {
            missionManager.deleteMission(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission.setId(null);
            missionManager.deleteMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission.setId(-1L);
            missionManager.deleteMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }

        try {
            mission.setId(1L);
            missionManager.deleteMission(mission);
            fail();
        } catch (IllegalArgumentException ex) {
            //Correct
        }
    }

    @Test
    public void testFindAllMissions() throws Exception {
        //assertThat(missionManager.findAllMissions(), is(empty())); <- useless, but good to know

        Mission mission1 = newMission("Assassination", "Oz", 5, null, null);
        missionManager.createMission(mission1);

        Mission mission2 = newMission("Poisoning", "Hungary", 5, null, null);
        missionManager.createMission(mission2);

        List<Mission> list = missionManager.findAvailableMissions();
        assertThat(list, contains(mission1, mission2));
    }

    @Test
    public void testFindAvailableMissions() throws Exception {
        Mission expectedMission = newMission("Assassination", "Oz", 5, null, null);
        missionManager.createMission(expectedMission);

        Mission otherMission = newMission("Poisoning", "Hungary", 5, LocalDate.of(2000,1,1), null);
        missionManager.createMission(otherMission);

        List<Mission> list = missionManager.findAvailableMissions();
        assertThat(list, hasItem(expectedMission));
        assertThat(list, not(hasItem(otherMission)));
    }

    @Test
    public void testFindCompletedMissions() throws Exception {
        Mission expectedMission = newMission("Assassination", "Oz", 5, LocalDate.of(2000,1,1), null);
        missionManager.createMission(expectedMission);

        Mission otherMission = newMission("Poisoning", "Hungary", 5, null, null);
        missionManager.createMission(otherMission);

        List<Mission> list = missionManager.findCompletedMissions();
        assertThat(list, hasItem(expectedMission));
        assertThat(list, not(hasItem(otherMission)));
    }

    private static Mission newMission(String title, String country, int requiredClearance, LocalDate dateOfCompletion, String description) {
        Mission mission = new Mission();
        mission.setTitle(title);
        mission.setCountry(country);
        mission.setRequiredClearance(requiredClearance);
        mission.setDateOfCompletion(dateOfCompletion);
        mission.setDescription(description);
        return mission;
    }
}