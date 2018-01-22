package model;

import java.io.Serializable;

/**
 * Created by Obrina.KIMI on 7/2/2017.
 */
public class Emploi implements Serializable {

    private static final long id = 1L;
    private String emploiId;
    private String title;
    private String description;
    private String webSite;
    private String salary;
    private String city;
    private String endDate;
    // top work great
    private String society;
    private String societyPicUrl;
    private String addDate;
    private String activitySector;
    private String email;
    private String mobile1;
    private String mobile2;
    private double longitude;
    private double latitude;

    private String sexe;
    private String contratType;
    private String workMode;
    private String experience;
    private String studyLevel;

    public String getSocietyPicUrl() {
        return societyPicUrl;
    }

    public void setSocietyPicUrl(String societyPicUrl) {
        this.societyPicUrl = societyPicUrl;
    }

    public String getEmploiId() {
        return emploiId;
    }

    public void setEmploiId(String emploiId) {
        this.emploiId = emploiId;
    }

    public String getSociety() {
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getActivitySector() {
        return activitySector;
    }

    public void setActivitySector(String activitySector) {
        this.activitySector = activitySector;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude == 0x00 ? 0 : longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude == 0x00 ? 0 : latitude;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getContratType() {
        return contratType;
    }

    public void setContratType(String contratType) {
        this.contratType = contratType;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(String studyLevel) {
        this.studyLevel = studyLevel;
    }

    public static long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
