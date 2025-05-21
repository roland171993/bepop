package model;

import android.support.annotation.NonNull;

import com.util.Constant;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Obrina.KIMI on 8/21/2017.
 */

public class LettreMotivation implements Serializable {

    private String id;
    private String title;
    private String content;

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

    public boolean fillFromJSON(@NonNull final JSONObject objJson){
        try {
            // Contient la Clé
            if (!objJson.isNull(Constant.LEMOTIVATION_ITEM_ID)){
                setId(objJson.getString(Constant.LEMOTIVATION_ITEM_ID.trim()));
            }
            if (!objJson.isNull(Constant.LEMOTIVATION_ITEM_TITLE)){
                setTitle(objJson.getString(Constant.LEMOTIVATION_ITEM_TITLE).trim());
            }
            if (!objJson.isNull(Constant.LEMOTIVATION_ITEM_CONTENT)){
                setContent(objJson.getString(Constant.LEMOTIVATION_ITEM_CONTENT));
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error er){
            er.printStackTrace();
        }
        return false;
    }
}
