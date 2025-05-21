package model;

import android.support.annotation.NonNull;

import com.util.Constant;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Obrina.KIMI on 8/21/2017.
 */

public class CV implements Serializable{

    private String id;
    private String title;
    private String content;
    private String downloadUrl;

    public String getDownloadUrl() {
        if (downloadUrl == null)
            downloadUrl = "";
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        if (id == null)
            id = "";
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        if (title == null)
            title = "";
        return title;
    }

    public boolean fillFromJSON(@NonNull final JSONObject objJson){
        try {

            // Contient la Clé

            if (!objJson.isNull(Constant.CV_ITEM_ID)){
                setId(objJson.getString(Constant.CV_ITEM_ID).trim());
            }
            if (!objJson.isNull(Constant.CV_ITEM_TITLE)){
                setTitle(objJson.getString(Constant.CV_ITEM_TITLE).trim());
            }
            if (!objJson.isNull(Constant.CV_ITEM_CONTENT)){
                setContent(objJson.getString(Constant.CV_ITEM_CONTENT));
            }
            if (!objJson.isNull(Constant.CV_ITEM_DOWNLOAD_URL)){
                setDownloadUrl(objJson.getString(Constant.CV_ITEM_DOWNLOAD_URL));
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error er){
            er.printStackTrace();
        }
        return false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        if (content == null)
            content = "";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
