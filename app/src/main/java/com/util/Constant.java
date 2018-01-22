package com.util;

import java.io.Serializable;

public class Constant implements Serializable{

 
	private static final long serialVersionUID = 1L;

	public static final String EMPLOIS_LATEST_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/fastEmploisLatest/get.awp";

    public static final String EMPLOIS_COMPLETE_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/emploisSorted/get.awp";
	//this url gives list of category in 2nd tab
    public static final String LETTRE_MOTIVATION_PART1_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/AllLeMotisPart1/get.awp";

    public static final String LETTRE_MOTIVATION_COMP_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/LeMotivations/get.awp";

    public static final String LETTRE_MOTIVATION_PART2_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/AllLeMotisPart2/get.awp?lemotisid=";

    public static final String LETTRE_MOTIVATION_DESC_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/leMotivationDesc/get.awp?idlemotivation=";

    public static final String EMPLOI_DESCRIPTION_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/emploiDesription/get.awp?emploiid=";
    //this url gives app version info
    public static final String APP_UPDATE_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/fastCurrentVersion/get.awp";
//  this url gives cgu info
    public static final String APP_CGU_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/Conditions-generales-utilisation.html";

    public static final String CV_FAST_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/fastCVLatest/get.awp";

    public static final String CV_COMPLETE_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/cvSorted/get.awp";

    public static final String CV_DESCRIPTION_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/cvDescription/get.awp?cvid=";

    public static final String MAP_INFO_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/MapAccess/get.awp";

    public static final String MAP_ACCOUNT_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/MapAccount/get.awp";

    public static final String ADVERTISER_URL = "http://www.stopgalere-ci.com/STOPGALERE_API_WEB/FR/api/Advertiser/get.awp";

    // Color Global string
    public static final String REFRESH_PROGRESS_BAR_COLOR = "#478fcc";

    public static final String THEME_ACCENT_COLOR = "#14499e"; // Color Accent fix getRessource.getColor -> deprecated

    public static final String COLOR_PRIMARY_DARK = "#3374ba";

//    this gives emploi item details
    public static final String EMPLOI_ITEM_ID = "nNUM_EMPLOI";
    public static final String EMPLOI_ITEM_TITLE = "sLibelle";
    public static final String EMPLOI_ITEM_DESCRI = "sDescription";
    public static final String EMPLOI_ITEM_EMAIL = "sEmail_Auteur";
    public static final String EMPLOI_ITEM_WEBSITE = "sSite_web_Auteur";
    public static final String EMPLOI_ITEM_SOCIETY = "sSociete";
    public static final String EMPLOI_ITEM_SOCIETY_IMAGE = "sSociete_image";
    public static final String EMPLOI_ITEM_ADDDATE = "sDate_ajout";
    public static final String EMPLOI_ITEM_SECTOR = "sNum_Sect_Activite";
    public static final String EMPLOI_ITEM_MOBILE2= "sMobile_n1_auteur";
    public static final String EMPLOI_ITEM_MOBILE1 = "sMobile_n2_auteur";
    public static final String EMPLOI_ITEM_LONGITUDE = "rLogitude_auteur";
    public static final String EMPLOI_ITEM_LATITUDE = "rLatitude_auteur";
    public static final String EMPLOI_ITEM_SALARY = "sSalaire";
    public static final String EMPLOI_ITEM_CITY = "sVille";
    public static final String EMPLOI_ITEM_SEXE = "sSexe";
    public static final String EMPLOI_ITEM_CONTRAT = "sType_Contrat";
    public static final String EMPLOI_ITEM_WORKMODE = "sMode_Travail";
    public static final String EMPLOI_ITEM_EXP = "sExperience";
    public static final String EMPLOI_ITEM_STUDYLEVEL = "sNiveau_etudes";
    public static final String EMPLOI_ITEM_ENDDATE = "sDate_Limite";

//    this gives Lettre motivations item details
    public static final String LEMOTIVATION_ITEM_ID = "sNum_LeMotivation";
    public static final String LEMOTIVATION_ITEM_TITLE = "sLibelleLeMoti";
    public static final String LEMOTIVATION_ITEM_CONTENT = "sContenu";
//    this gives CV item details
    public static final String CV_ITEM_ID = "sNum_CV";
    public static final String CV_ITEM_TITLE = "sLibelleCV";
    public static final String CV_ITEM_CONTENT = "sContenu";
    public static final String CV_ITEM_DOWNLOAD_URL = "sUrlTelechargement";
//    this gives item for map
    public static final String MAPS_ACCES = "sAllow";
    public static final String MAPS_LOCK = "sGPSLock";
    public static final String MAPS_LOCATION_HOME = "sMapPos";
    public static final String MAPS_LOCATION_DEST = "sMapIti";
    public static final String MAPS_LOCATION_REFRESH = "sMapsRefresh";

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

 
}
