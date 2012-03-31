package com.bukkit.gemo.BukkitHTTP;

import java.util.HashMap;
import java.util.List;

public class HTTPEvent {
    public String completeFileName;
    public String fileName;
    public List<String> cookies;
    public HashMap<String, String> getParameter;
    public HashMap<String, String> postParameter;
    public boolean isGetMethod = true;

    public HTTPEvent(String completeFileName, HashMap<String, String> getParameter, HashMap<String, String> postParameter, List<String> cookies, boolean isGetMethod) {
        this.completeFileName = completeFileName;
        this.getParameter = getParameter;
        this.postParameter = postParameter;
        this.fileName = getFileName(this.completeFileName);
        this.isGetMethod = isGetMethod;
        this.cookies = cookies;
    }

    private String getFileName(String completeFileName) {
        String[] split = completeFileName.split("/");
        return split[split.length - 1];
    }

    // /////////////////////
    //
    // GET COOKIE PARAM
    //
    // /////////////////////
    public String getCookieParam(String paramName) {
        for (String cookie : this.cookies) {
            String[] split = cookie.split("#");
            if (split.length < 1)
                return "";

            for (String param : split) {
                String[] paramSplit = param.split("=");
                if (paramSplit.length != 2)
                    continue;

                if (paramSplit[0].equalsIgnoreCase(paramName)) {
                    return paramSplit[1];
                }
            }
        }
        return "";
    }

    // /////////////////////
    //
    // GET ALL COOKIES
    //
    // /////////////////////
    public List<String> getCookie() {
        return this.cookies;
    }
}
