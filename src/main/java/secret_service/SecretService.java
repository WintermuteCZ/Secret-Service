package secret_service;

import java.util.List;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public interface SecretService {
    void assignAgentToMission(SecretAgent secretAgent, Mission mission);

    void removeAgentFromMission(SecretAgent secretAgent, Mission mission);

    List<Mission> findMissionsWithAgent(SecretAgent secretAgent);

    SecretAgent findAgentOnMission(Mission mission);

}
