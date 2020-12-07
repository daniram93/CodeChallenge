package com.OrderExecution;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;


public class JSONReader {

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static ArrayList<Map<String,String>> readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            Map<String,Object> jsonCatalog = new ObjectMapper().readValue(jsonText, HashMap.class);
            Map<String,Object> listCatalogPhones = (Map<String, Object>) jsonCatalog.get("_embedded");
            ArrayList<Map<String,String>> catalogPhones = (ArrayList<Map<String, String>>) listCatalogPhones.get("phones");
            return catalogPhones;
        } finally {
            is.close();
        }
    }
}
