package secret_service;

import java.time.LocalDate;

/**
 * Created by Vitus-ad on 26. 2. 2015.
 */
public class Mission {
    private Long id;
    private String title;
    private String country;
    private String description;
    private LocalDate dateOfCompletion;
    private int requiredClearance;

    public Mission() {

    }

    public Mission(Long id, String title, String country, String description, LocalDate dateOfCompletion, int requiredClearance) {
        this.id = id;
        this.title = title;
        this.country = country;
        this.description = description;
        this.dateOfCompletion = dateOfCompletion;
        this.requiredClearance = requiredClearance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfCompletion() {
        return dateOfCompletion;
    }

    public void setDateOfCompletion(LocalDate dateOfCompletion) {
        this.dateOfCompletion = dateOfCompletion;
    }

    public int getRequiredClearance() {
        return requiredClearance;
    }

    public void setRequiredClearance(int requiredClearance) {
        this.requiredClearance = requiredClearance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mission)) return false;

        Mission mission = (Mission) o;

        if (!id.equals(mission.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                ", dateOfCompletion=" + dateOfCompletion +
                ", requiredClearance=" + requiredClearance +
                '}';
    }
}