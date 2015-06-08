package cz.muni.fi.pv168.secret_service;

import java.util.List;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public interface AgentManager {
    void createAgent(SecretAgent secretAgent);

    void updateAgent(SecretAgent secretAgent);

    void deleteAgent(SecretAgent secretAgent);

    List<SecretAgent> findAllAgents();

    List<SecretAgent> findAliveAgents();

    SecretAgent findAgentByID(Long id);
}
