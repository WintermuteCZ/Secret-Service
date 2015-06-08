package web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.muni.fi.pv168.secret_service.AgentManager;
import cz.muni.fi.pv168.secret_service.SecretAgent;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Servlet for managing agents.
 */
@WebServlet(AgentServlet.URL_MAPPING + "/*")
public class AgentServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/agent";

    private final static Logger log = LoggerFactory.getLogger(AgentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showAgentsList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //aby fungovala čestina z formuláře
        request.setCharacterEncoding("utf-8");
        //akce podle přípony v URL
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //načtení POST parametrů z formuláře
                String name = request.getParameter("name");
                String gender = request.getParameter("gender");
                String birth = request.getParameter("birth");
                String death = request.getParameter("death");
                String clearance = request.getParameter("clearance");
                //kontrola vyplnění hodnot
                if (name == null || name.length() == 0 || gender == null || gender.length() == 0 || birth == null ||
                        birth.length() == 0 || clearance == null || clearance.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty kromě smrti!");
                    showAgentsList(request, response);
                    return;
                }
                LocalDate birthDate;
                LocalDate deathDate;
                int clearanceLevel;
                try {
                    String[] tok = birth.split("-");
                    birthDate = LocalDate.of(Integer.parseInt(tok[2]), Integer.parseInt(tok[1]), Integer.parseInt(tok[0]));
                    if (death == null || death.length() == 0)
                        deathDate = null;
                    else {
                        tok = death.split("-");
                        deathDate = LocalDate.of(Integer.parseInt(tok[2]), Integer.parseInt(tok[1]), Integer.parseInt(tok[0]));
                    }
                }
                catch(Exception e) {
                    request.setAttribute("chyba", "Wrong date format!");
                    showAgentsList(request, response);
                    return;
                }
                try {
                    clearanceLevel = Integer.parseInt(clearance);
                }
                catch (Exception e) {
                    request.setAttribute("chyba", "Clearance level must be number.!");
                    showAgentsList(request, response);
                    return;
                }
                //zpracování dat - vytvoření záznamu v databázi
                try {
                    SecretAgent agent = new SecretAgent(null, name, gender, birthDate, deathDate, clearanceLevel);
                    getAgentManager().createAgent(agent);
                    log.debug("created {}",agent);
                    //redirect-after-POST je ochrana před vícenásobným odesláním formuláře
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    log.error("Cannot add agent", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    SecretAgent agent = getAgentManager().findAgentByID(id);
                    getAgentManager().deleteAgent(agent);
                    log.debug("deleted agent {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (Exception e) {
                    log.error("Cannot delete agent", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //bonus
                return;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    /**
     * Gets AgentManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return AgentManager instance
     */
    private AgentManager getAgentManager() {
        return (AgentManager) getServletContext().getAttribute("agentManager");
    }

    /**
     * Stores the list of books to request attribute "agents" and forwards to the JSP to display it.
     */
    private void showAgentsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("agents", getAgentManager().findAllAgents());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (Exception e) {
            log.error("Cannot show agents", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
