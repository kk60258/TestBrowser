package org.mozilla.materialfennec.search;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.materialfennec.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nineg on 2017/9/17.
 */

public class SearchHttpHelper {
    public static final String GOOGLE_SEARCH_HOST = "https://www.google.com.tw/";
    private static final String GOOGLE_SEARCH_API = "https://www.google.com.tw/search?q=";
    private static final String SERVER_HOST = "https://ac.duckduckgo.com/";
    private static final String SEARCH_HOST_QURY_APPENDANT = "?ko=-1&kl=wt-wt";
    private static final String SUGGESTION_API = "ac/";
    private static final String SEARCH_PARAM = "q";

    @NonNull
    public static String getSearchUrl(@NonNull String keyword) {
//        StringBuilder builder = new StringBuilder(SERVER_HOST);
//        builder.append(SEARCH_HOST_QURY_APPENDANT).append("&").append(SEARCH_PARAM).append("=").append(getEncodedQuery(keyword));
//        return builder.toString();
        return GOOGLE_SEARCH_API + getEncodedQuery(keyword);
    }

    @NonNull
    private static String getSuggestionUrl(@NonNull String keyword) {
        StringBuilder builder = new StringBuilder(SERVER_HOST);
        builder.append(SUGGESTION_API).append("?").append(SEARCH_PARAM).append("=").append(getEncodedQuery(keyword));
        return builder.toString();
    }

    @NonNull
    private static String getEncodedQuery(@NonNull String keyword) {
        String encoded = keyword;
        try {
            encoded = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e(SearchHttpHelper.class.getSimpleName(), "getEncodedQuery %s ,%s", keyword, e.getMessage());
        }
        return encoded;
    }

    public static List<String> getSuggestions(@NonNull String keyword) {
        List<String> result = new ArrayList<>();
        try {
            String urlString = getSuggestionUrl(keyword);
            Logger.d(SearchHttpHelper.class.getSimpleName(), "getSuggestions %s", urlString);
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                result = parseFromJson(jsonArray);
            }
        } catch (MalformedURLException e) {
            Logger.e(SuggestionAsyncTask.class.getSimpleName(), "getSuggestions, query: %s, msg %s", keyword, e.getMessage());
        } catch (IOException e) {
            Logger.e(SuggestionAsyncTask.class.getSimpleName(), "getSuggestions, query: %s", keyword, e.getMessage());
        } catch (JSONException e) {
            Logger.e(SuggestionAsyncTask.class.getSimpleName(), "getSuggestions, query: %s", keyword, e.getMessage());
        }
        return result;
    }

    private static List<String> parseFromJson(JSONArray jsonArray) throws JSONException {
        List<String> result = new ArrayList<>();
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String key = jsonObject.keys().next();
            String value = jsonObject.getString(key);
            result.add(value);
            Logger.d(SearchHttpHelper.class.getSimpleName(), "parseFromJson %s, %s", key, value);
        }
        return result;
    }
}
