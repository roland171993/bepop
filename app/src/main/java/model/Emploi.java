package model;

import android.support.annotation.NonNull;

import com.util.Constant;

import org.json.JSONObject;

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


    public boolean fillFromJSON(@NonNull final  JSONObject objJson){
        try {

            // Contient la Clé
            if (!objJson.isNull(Constant.EMPLOI_ITEM_ID)){
                setEmploiId(objJson.getString(Constant.EMPLOI_ITEM_ID).trim());

            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_TITLE)){
                setTitle(objJson.getString(Constant.EMPLOI_ITEM_TITLE).trim());

            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_ADDDATE)){
                setAddDate(objJson.getString(Constant.EMPLOI_ITEM_ADDDATE));
            }

            if (!objJson.isNull(Constant.EMPLOI_ITEM_EMAIL)){
                setEmail(objJson.getString(Constant.EMPLOI_ITEM_EMAIL).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_WEBSITE)){
                setWebSite(objJson.getString(Constant.EMPLOI_ITEM_WEBSITE).trim());

            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_MOBILE1)){
                setMobile1(objJson.getString(Constant.EMPLOI_ITEM_MOBILE1).trim());

            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_MOBILE2)){
                setMobile2(objJson.getString(Constant.EMPLOI_ITEM_MOBILE2).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_SALARY)){
                setSalary(objJson.getString(Constant.EMPLOI_ITEM_SALARY).trim());
            }

            if (!objJson.isNull(Constant.EMPLOI_ITEM_CITY)){
                setCity(objJson.getString(Constant.EMPLOI_ITEM_CITY).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_ENDDATE)){
                setEndDate(objJson.getString(Constant.EMPLOI_ITEM_ENDDATE));
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_DESCRI)){
                setDescription(objJson.getString(Constant.EMPLOI_ITEM_DESCRI).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_STUDYLEVEL)){
                setStudyLevel(objJson.getString(Constant.EMPLOI_ITEM_STUDYLEVEL).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_SEXE)){
                JSONObject sexeObj = objJson.getJSONObject(Constant.EMPLOI_ITEM_SEXE);
                if (!sexeObj.isNull("libelle")){
                    setSexe(sexeObj.getString("libelle"));
                }
            }

            if (!objJson.isNull(Constant.EMPLOI_ITEM_SOCIETY)){
                setSociety(objJson.getString(Constant.EMPLOI_ITEM_SOCIETY).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_EXP)){
                setExperience(objJson.getString(Constant.EMPLOI_ITEM_EXP).trim());
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_SOCIETY_IMAGE)){
                setSocietyPicUrl(objJson.getString(Constant.EMPLOI_ITEM_SOCIETY_IMAGE.trim()));
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_LATITUDE)){
                setLatitude(objJson.getDouble(Constant.EMPLOI_ITEM_LATITUDE));
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_LONGITUDE)){
                setLongitude(objJson.getDouble(Constant.EMPLOI_ITEM_LONGITUDE));
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_WORKMODE)){
                JSONObject son = objJson.getJSONObject(Constant.EMPLOI_ITEM_WORKMODE);
                if (!son.isNull("libelle")){
                    setWorkMode(son.getString("libelle"));
                }
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_SECTOR)){
                JSONObject son = objJson.getJSONObject(Constant.EMPLOI_ITEM_SECTOR);
                if (!son.isNull("libelle")){
                    setActivitySector(son.getString("libelle"));
                }
            }
            if (!objJson.isNull(Constant.EMPLOI_ITEM_CONTRAT)){
                JSONObject son =objJson.getJSONObject(Constant.EMPLOI_ITEM_CONTRAT);
                if (!son.isNull("libelle")){
                    setContratType(son.getString("libelle"));
                }
            }



            return true;
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error er){
            er.printStackTrace();
        }
        return false;
    }

    public String getSocietyPicUrl() {
        if (societyPicUrl == null)
            societyPicUrl = "";
        return societyPicUrl;
    }

    public void setSocietyPicUrl(String societyPicUrl) {
        this.societyPicUrl = societyPicUrl;
    }

    public String getEmploiId() {
        if (emploiId == null )
            emploiId = "";
        return emploiId;
    }

    public void setEmploiId(String emploiId) {
        this.emploiId = emploiId;
    }

    public String getSociety() {
        if (society == null)
            society = "";
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public String getAddDate() {
        if (addDate == null)
            addDate = "";
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getActivitySector() {
        if (activitySector == null)
            activitySector = "";
        return activitySector;
    }

    public void setActivitySector(String activitySector) {
        this.activitySector = activitySector;
    }

    public String getEmail() {
        if (email == null)
            email ="";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile1() {
        if (mobile1 == null)
            mobile1 = "";
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        if (mobile2 == null)
            mobile2 = "";
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
        if (sexe == null)
            sexe = "";
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getContratType() {
        if (contratType == null)
            contratType = "";
        return contratType;
    }

    public void setContratType(String contratType) {
        this.contratType = contratType;
    }

    public String getWorkMode() {
        if (workMode == null)
            workMode = "";
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getExperience() {
        if (experience == null)
            experience ="";
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getStudyLevel() {
        if (studyLevel == null)
            studyLevel = "";
        return studyLevel;
    }

    public void setStudyLevel(String studyLevel) {
        this.studyLevel = studyLevel;
    }

    public static long getId() {
        return id;
    }

    public String getTitle() {
        if (title == null)
            title = "";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if (description == null)
            description = "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebSite() {
        if (webSite == null)
            webSite = "";
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getSalary() {
        if (salary == null)
            salary = "";
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getCity() {
        if (city == null)
            city = "";
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEndDate() {
        if (endDate == null)
            endDate ="";
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
