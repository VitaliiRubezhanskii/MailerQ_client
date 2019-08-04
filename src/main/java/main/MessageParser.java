package main;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageParser {

    public static void main(String[] args) throws IOException {
        String regex = "^(https?:\\\\/\\\\/)?(www\\.)?([\\\\w]+\\\\.)+[\u200C\u200B\\\\w]{2,63}\\\\/?$";
        readJson(regex);
    }
    private static Object readJson(String mask) throws IOException {
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        Map<String, String> urlsToShorten = null;
        Map<String, String> shortenedUrls = null;
        LinkedTreeMap jsonObject = null;
        Map<String, String> resulting = null;
        try (Reader reader = new FileReader("src/main/resources/message.json")) {

             jsonObject = gson.fromJson(reader, LinkedTreeMap.class);
             urlsToShorten = parse(mask, jsonObject, map);
             shortenedUrls = shortener(urlsToShorten);
             resulting = parseBack(jsonObject, shortenedUrls);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("initial object: " + jsonObject);
        System.out.println("-----------------------------");
        System.out.println("shortened: " + shortenedUrls);
        System.out.println("-----------------------------");
        System.out.println("resulting: " + resulting);
        return null;
    }

    private static Map<String, String> parse(String mask, LinkedTreeMap treeMap, Map<String, String> map) {
        for (String key : (Set<String>)treeMap.keySet()) {
            if (treeMap.get(key) instanceof LinkedTreeMap) {
                parse(mask, (LinkedTreeMap) treeMap.get(key), map);
            }
            if (treeMap.get(key) instanceof ArrayList) {
                ((List) treeMap.get(key)).stream().forEach(k -> parse(mask, (LinkedTreeMap) k, map));
            }
            if (treeMap.get(key) instanceof String && ((String) treeMap.get(key)).contains("http")){
                String id =  "shortener-index-".concat(UUID.randomUUID().toString());
                map.put(id, (String) treeMap.get(key)); // TODO: generate object for shortener logic
                treeMap.put(key, id);

            }
        }
        return map;
    }

    private static Map<String, String> shortener(Map<String, String> urlMap){
        urlMap.forEach((k, v) -> urlMap.computeIfPresent(k, (x,y) -> y.concat("_shortened"))); // TODO: here is URL shortener pseudo logic
        return urlMap;
    }


    private static Map<String, String> parseBack( LinkedTreeMap treeMap, Map<String, String> map) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(treeMap);
        for (String key : (Set<String>)concurrentHashMap.keySet()) {
            if (treeMap.get(key) instanceof LinkedTreeMap) {
                parseBack((LinkedTreeMap) concurrentHashMap.get(key), map);
            }
            if (treeMap.get(key) instanceof ArrayList) {
                ((List) concurrentHashMap.get(key)).stream().forEach(k -> parseBack((LinkedTreeMap) k, map));
            }
            if (concurrentHashMap.get(key) instanceof String && ((String) concurrentHashMap.get(key)).contains("shortener-index")){

                concurrentHashMap.forEach((k, v)->treeMap.computeIfPresent(k, (x, y)-> map.get(y))); // TODO: appending shortener results back
            }
        }
        return concurrentHashMap;
    }

    private static boolean IsMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

}
