package secret_service;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public interface SecretService {
    void assignAgentToMission(SecretAgent secretAgent, Mission mission);

    void removeAgentFromMission(SecretAgent secretAgent, Mission mission);

    Mission findMissionWithAgent(SecretAgent secretAgent);

    Agent findAgentOnMission(Mission mission);

}
