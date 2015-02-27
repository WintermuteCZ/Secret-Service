package secret_service;

import java.util.Date;

/*Hello there sweetheart*/
/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public class SecretAgent {

    Long id;
    private String name;
    private String gender;
    private java.util.Date dateOfBirth;
    private java.util.Date dateOfDeath;


    public SecretAgent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }
}
