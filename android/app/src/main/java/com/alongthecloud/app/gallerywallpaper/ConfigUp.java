package com.alongthecloud.app.gallerywallpaper;

import android.os.Build;
import androidx.annotation.RequiresApi;
import android.content.Context;

import org.json.*;
import java.util.*;

public class ConfigUp {
    public static String SOCKET_ADDR = "com.alongthecloud.gallerywallpaper";

    // Singleton
    private static ConfigUp ourInstance = null;

    public static ConfigUp getInstance() {
        if (ourInstance == null)
            ourInstance = new ConfigUp();

        return ourInstance;
    }

    public static void releaseInstance() {
        ourInstance = null;
    }

    private ConfigUp() {
    }

    SharedPrefUtil sharedprefutil = null;

    public SharedPrefUtil initSharedPref(Context context) {
        sharedprefutil = new SharedPrefUtil(context);
        return sharedprefutil;
    }

    class Config {
        public List<String> pictures;   // url (local, network)
        public int fillType;        // 'fill', 'stretch', 'tile', 'center'
        public Boolean sufffle;     // suffle or orderd
        public int timeSec;         // per sec
    }

    Config config = null;
    int index = -1;

    public boolean loadFromSharedPrefs() {
        // String jsonText = "{\"pictures\":[\"/data/user/0/com.alongthecloud.app.gallerywallpaper/cache/image_picker8865273403880149713.jpg\",\"/data/user/0/com.alongthecloud.app.gallerywallpaper/cache/image_picker2087104456584553380.jpg\"],\"filltype\":0,\"suffle\":false}";
        String jsonText = sharedprefutil.getStr("config");
        if (jsonText == null)   return false;
        if (jsonText.length() == 0) return false;

        updateConfig(jsonText);

        if (config == null) {
            return false;
        }

        index = -1;
        return true;
    }

    public int getTimeSec() {
        if (config == null)
            return 1;
        else
            return config.timeSec;
    }

    public int getFillType() {
        final int DEFAULT_FILL = 2;

        if (config == null) return DEFAULT_FILL;
        return config.fillType;
    }

    public String getPictureUrl() {
        final String EMPTY = "";

        if (config == null) return EMPTY;
        List<String> pictures = config.pictures;
        if (pictures == null) return EMPTY;
        if (pictures.size() == 0) return EMPTY;

        if (index < 0 || index >= pictures.size() ) return EMPTY;

        return pictures.get(index);
    }

    public void nextPicture() {
        if (config == null) return;
        List<String> pictures = config.pictures;
        if (pictures == null) return;

        ++index;
        if (index >= pictures.size()) index = 0;
    }

    public void updateConfig(String text) {
        MyLog.d("ConfigUp", text);

        config = new Config();
        config.pictures = new ArrayList<String>();
        config.fillType = 1;
        config.sufffle = false;
        config.timeSec = 10;

        try {
            JSONObject json = new JSONObject(text);
            JSONArray movieArray = json.getJSONArray("pictures");

            for (int i = 0; i < movieArray.length(); i++) {
                String url = movieArray.getString(i);
                config.pictures.add(url);
            }

           config.fillType = json.getInt("filltype");
           config.sufffle = json.getBoolean("suffle");
           config.timeSec = json.getInt("timesec");

        } catch (JSONException e) {
            e.printStackTrace();
            // config = null;
        }
    }
}
