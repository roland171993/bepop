package com.util;

import java.io.Serializable;

public class Constant implements Serializable{

 
	private static final long serialVersionUID = 1L;

    public static final String BASE_URL =                       "http://51.75.26.88/";

	public static final String EMPLOIS_LATEST_URL =             BASE_URL+"stopgalereapi_get/fastEmploisLatest";

    public static final String EMPLOIS_COMPLETE_URL =           BASE_URL+"stopgalereapi_get/emploisSorted";
	//this url gives list of category in 2nd tab
    public static final String LETTRE_MOTIVATION_PART1_URL =    BASE_URL+"stopgalereapi_get/AllLeMotisPart1";

    public static final String LETTRE_MOTIVATION_COMP_URL =     BASE_URL+"stopgalereapi_get/LeMotivationsCompleteApi";

    public static final String LETTRE_MOTIVATION_DESC_URL =     BASE_URL+"stopgalereapi_get/LeMotivationsForIdApi/";

    public static final String EMPLOI_DESCRIPTION_URL =         BASE_URL+"stopgalereapi_get/emploiDesription/";
    //this url gives app version info
    public static final String APP_UPDATE_URL =                 BASE_URL+"stopgalereapi_get/fastCurrentVersion";
//  this url gives cgu info
    public static final String APP_CGU_URL =                    BASE_URL+"stopgalere/condition-cgu";

    public static final String CV_FAST_URL =                    BASE_URL+"stopgalereapi_get/cvlastapp";

    public static final String CV_COMPLETE_URL =                BASE_URL+"stopgalereapi_get/cvssortedapp";

    public static final String CV_DESCRIPTION_URL =             BASE_URL+"stopgalereapi_get/cvDescriptionapp/";

    public static final String MAP_INFO_URL =                   BASE_URL+"stopgalereapi_get/mapaccess";

    public static final String MAP_ACCOUNT_URL =                BASE_URL+"stopgalereapi_get/mapaccount";

    public static final String ADVERTISER_URL =                 BASE_URL+"stopgalereapi_get/advertiser";

    // Color Global string
    public static final String REFRESH_PROGRESS_BAR_COLOR = "#478fcc";

    public static final String THEME_ACCENT_COLOR = "#14499e"; // Color Accent fix getRessource.getColor -> deprecated

    public static final String COLOR_PRIMARY_DARK = "#3374ba";

//    this gives emploi item details
    public static final String EMPLOI_ITEM_ID = "_id";
    public static final String EMPLOI_ITEM_TITLE = "libelle";
    public static final String EMPLOI_ITEM_DESCRI = "description";
    public static final String EMPLOI_ITEM_EMAIL = "email_auteur";
    public static final String EMPLOI_ITEM_WEBSITE = "site_web_auteur";
    public static final String EMPLOI_ITEM_SOCIETY = "societe";
    public static final String EMPLOI_ITEM_SOCIETY_IMAGE = "societe_img_url";
    public static final String EMPLOI_ITEM_ADDDATE = "date_ajout";
    public static final String EMPLOI_ITEM_SECTOR = "_id_setc_activite";
    public static final String EMPLOI_ITEM_MOBILE2= "mobile_n1_auteur";
    public static final String EMPLOI_ITEM_MOBILE1 = "mobile_n2_auteur";
    public static final String EMPLOI_ITEM_LONGITUDE = "logitude_auteur";
    public static final String EMPLOI_ITEM_LATITUDE = "latitude_auteur";
    public static final String EMPLOI_ITEM_SALARY = "salaire";
    public static final String EMPLOI_ITEM_CITY = "ville";
    public static final String EMPLOI_ITEM_SEXE = "_id_sexe";
    public static final String EMPLOI_ITEM_CONTRAT = "_id_type_contrat";
    public static final String EMPLOI_ITEM_WORKMODE = "_id_mode_travail";
    public static final String EMPLOI_ITEM_EXP = "experience";
    public static final String EMPLOI_ITEM_STUDYLEVEL = "niveau_etudes";
    public static final String EMPLOI_ITEM_ENDDATE = "date_limite";

//    this gives Lettre motivations item details
    public static final String LEMOTIVATION_ITEM_ID = "_id";
    public static final String LEMOTIVATION_ITEM_TITLE = "libelle";
    public static final String LEMOTIVATION_ITEM_CONTENT = "contenu";
//    this gives CV item details
    public static final String CV_ITEM_ID = "_id";
    public static final String CV_ITEM_TITLE = "libelle";
    public static final String CV_ITEM_CONTENT = "contenu";
    public static final String CV_ITEM_DOWNLOAD_URL = "url_telechargement";
//    this gives item for map
    public static final String MAPS_ACCES = "allow";
    public static final String MAPS_LOCK = "gpsLock";
    public static final String MAPS_LOCATION_HOME = "mapPos";
    public static final String MAPS_LOCATION_DEST = "mapIti";
    public static final String MAPS_LOCATION_REFRESH = "mapsRefresh";

    public static final int COMPANY_RANDOM_NUMBER = 2;
    public static final int ACTIVITY_FINISH = 1;
    public static final int ACTIVITY_NOT_FINISH = 2;

    public static final long REQUEST_EMPLOI_DELAY = 7000;
    public static final long REQUEST_CV_DELAY = 4000;

    //Ads Adolony
    public static final String ADCOLONY_APP_ID = "app3c67cbf46817418a85";
    public static final String ADCOLONY_ZONE_ID = "vz1ea1949b726043d29c";

    //Ad Mobile
    public static final int AD_NETWORK_STARTAPP = 2;
    public static final int AD_NETWORK_IRONSOURCE = 3;

    public static final int REQUEST_GROUP_PERMISSION = 425;

 
}
