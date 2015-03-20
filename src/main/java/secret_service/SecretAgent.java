package secret_service;

import java.time.LocalDate;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public class SecretAgent {

    private Long id;
    private String name;
    private String gender;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private int clearanceLevel;

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }


    public int getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(int clearanceLevel) {
        this.clearanceLevel = clearanceLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecretAgent)) return false;

        SecretAgent that = (SecretAgent) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}