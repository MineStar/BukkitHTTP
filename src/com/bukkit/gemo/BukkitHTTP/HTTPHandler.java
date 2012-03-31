package com.bukkit.gemo.BukkitHTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HTTPHandler implements HttpHandler {
    // VARIABLES
    private String webfolder = "plugins/";
    public HashMap<String, HTTPPlugin> registeredPlugins = new HashMap<String, HTTPPlugin>();

    // /////////////////////
    //
    // CONSTRUCTOR
    //
    // /////////////////////
    public HTTPHandler() {
    }

    // /////////////////////
    //
    // GET FILENAME
    //
    // /////////////////////
    private String getFilename(String string) {
        String[] split = string.split("/");
        return split[split.length - 1];
    }

    // /////////////////////
    //
    // GET DIRECTORY
    //
    // /////////////////////
    private String getDirectory(String path, String filename) {
        return path.replace(filename, "");
    }

    // /////////////////////
    //
    // GET ROOT DIRECTORY
    //
    // /////////////////////
    private String getRootDir(String string) {
        String[] split = string.substring(1).split("/");
        return split[0];
    }

    // /////////////////////
    //
    // GET REQUESTED FILE
    //
    // /////////////////////
    private String getRequestedFile(String fullpath, String root) {
        return "/" + fullpath.replace("/" + root + "/", "");
    }

    // /////////////////////
    //
    // GET GET-PARAMETER
    //
    // /////////////////////
    public HashMap<String, String> getGetParameter(String filename) {
        filename = filename.trim();
        HashMap<String, String> result = new HashMap<String, String>();
        filename = filename.replace("?", "#");
        String[] split = filename.split("#");

        if (split.length < 2) {
            return result;
        }
        String[] paramSplit = split[1].split("&");
        String[] resSplit;
        for (String part : paramSplit) {
            resSplit = part.split("=");
            if (resSplit.length == 2) {
                result.put(resSplit[0], resSplit[1]);
            }
        }
        return result;
    }

    // /////////////////////
    //
    // GET POST-PARAMETER
    //
    // /////////////////////
    public HashMap<String, String> getPostParameter(String data) {
        data = data.trim();
        HashMap<String, String> result = new HashMap<String, String>();

        String[] paramSplit = data.split("&");
        String[] resSplit;
        for (String part : paramSplit) {
            resSplit = part.split("=");
            if (resSplit.length == 2) {
                result.put(resSplit[0], resSplit[1]);
            }
        }
        return result;
    }

    // /////////////////////
    //
    // IS FOLDER
    //
    // /////////////////////
    private boolean isFolder(String filename) {
        filename = filename.trim();
        return filename.endsWith("/");
    }

    // /////////////////////
    //
    // FILE EXISTS
    //
    // /////////////////////
    private boolean fileExists(String filename) {
        boolean found = new File(filename).exists();
        if (found)
            return new File(filename).isFile();

        return found;
    }

    // /////////////////////
    //
    // IS USER LOGGED IN
    //
    // /////////////////////
    private boolean userIsLoggedIn(String cookie) {
        String[] split = cookie.split("#");
        if (split.length < 1)
            return false;

        String param = split[0];
        String[] paramSplit = param.split("=");
        if (paramSplit.length != 2)
            return false;

        if (paramSplit[0].equalsIgnoreCase("LoggedIn") && paramSplit[1].equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    // /////////////////////
    //
    // CHECK LOGIN
    //
    // /////////////////////
    public boolean checkLogin(String data) {
        return true;
    }

    // /////////////////////
    //
    // CONVERT STREAM
    //
    // /////////////////////
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // /////////////////////
    //
    // GET COOKIE FROM HEADER
    //
    // /////////////////////
    private List<String> getCookiesFromHeader(HttpExchange exchange) {
        Headers head = exchange.getRequestHeaders();
        List<String> cookie = head.get("Cookie");
        if (cookie != null) {
            if (cookie.size() > 0) {
                return cookie;
            }
        }
        return new ArrayList<String>();
    }

    // /////////////////////
    //
    // GET COOKIE LOGIN VALUE
    //
    // /////////////////////
    private boolean getCookieLogin(HttpExchange exchange) {
        Headers head = exchange.getRequestHeaders();
        List<String> cookie = head.get("Cookie");
        boolean f = false;
        if (cookie != null) {
            if (cookie.size() > 0) {
                for (String tmp : cookie) {
                    f = userIsLoggedIn(tmp);
                    if (f)
                        return true;
                }
            }
        }
        return false;
    }

    // /////////////////////
    //
    // HANDLE REQUEST
    //
    // /////////////////////
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String data = convertStreamToString(exchange.getRequestBody());

        String fullpath = exchange.getRequestURI().getPath().toString();

        if (isFolder(fullpath)) {
            fullpath += "index.html";
        }

        String filename = getFilename(fullpath);
        if (filename == null || filename.length() < 1) {
            filename += "index.html";
        }
        String dir = getDirectory(fullpath, filename);
        String root = getRootDir(dir);
        String reqFile = getRequestedFile(fullpath, root);

        List<String> cookies = getCookiesFromHeader(exchange);
        if (registeredPlugins.containsKey(root)) {
            HTTPPlugin plugin = registeredPlugins.get(root);

            String contentType = getContentType(root + reqFile);
            Headers responseHeaders = exchange.getResponseHeaders();
            OutputStream responseBody = exchange.getResponseBody();

            if (!fileExists(webfolder + plugin.getRoot() + reqFile) && !filename.equalsIgnoreCase(plugin.getCheckLoginPage())) {
                if (!filename.endsWith("js"))
                    responseHeaders.set("Content-Type", "text/html");
                else
                    responseHeaders.set("Content-Type", "text/javascript");
                if (!plugin.isOwn404Page() || !fileExists(webfolder + plugin.getRoot() + "/" + plugin.getError404Page())) {
                    exchange.sendResponseHeaders(404, 0);
                    responseBody.write(("Error 404 - Page '" + webfolder + root + reqFile + "' not found").getBytes());
                } else {
                    exchange.sendResponseHeaders(200, 0);
                    Page page = new Page(webfolder + plugin.getRoot() + "/" + plugin.getError404Page());
                    HTTPEvent event = new HTTPEvent(webfolder + plugin.getRoot() + "/" + plugin.getError404Page(), getGetParameter(fullpath), null, cookies, true);
                    plugin.handle404Page(page, event);
                    responseBody.write(page.line.getBytes());
                }
                responseBody.close();
            }

            responseHeaders.set("Content-Type", contentType);
            if (contentType.equalsIgnoreCase("text/html")) {
                boolean loggedIn = true;
                if (plugin.isUseAuth()) {
                    loggedIn = this.getCookieLogin(exchange);
                }

                if (!plugin.isUseAuth() || loggedIn) {
                    exchange.sendResponseHeaders(200, 0);
                    Page page = new Page(webfolder + plugin.getRoot() + reqFile);
                    if (requestMethod.equalsIgnoreCase("POST")) {
                        HTTPEvent event = new HTTPEvent(webfolder + plugin.getRoot() + reqFile, getGetParameter(exchange.getRequestURI().toASCIIString()), getPostParameter(data), cookies, false);
                        plugin.handlePostRequest(page, event);
                    } else if (requestMethod.equalsIgnoreCase("GET")) {
                        HTTPEvent event = new HTTPEvent(webfolder + plugin.getRoot() + reqFile, getGetParameter(exchange.getRequestURI().toASCIIString()), null, cookies, true);
                        plugin.handleGetRequest(page, event);
                    }
                    responseBody.write(page.line.getBytes());
                } else {
                    if (plugin.isUseAuth() && !filename.equalsIgnoreCase(plugin.getCheckLoginPage())) {
                        // REDIRECT TO LOGIN
                        exchange.sendResponseHeaders(200, 0);
                        Page page = new Page(webfolder + plugin.getRoot() + "/" + plugin.getLoginPage());
                        HTTPEvent event = new HTTPEvent(webfolder + plugin.getRoot() + "/" + plugin.getLoginPage(), null, null, cookies, true);
                        plugin.handleGetRequest(page, event);
                        responseBody.write(page.line.getBytes());
                    } else {
                        // CHECK LOGIN
                        HTTPEvent event = new HTTPEvent(webfolder + plugin.getRoot() + "/" + plugin.getCheckLoginPage(), getGetParameter(exchange.getRequestURI().toASCIIString()), getPostParameter(data), cookies, false);
                        String login = plugin.loginSuccessful(event);
                        if (login != null) {
                            // SET COOKIES
                            responseHeaders.set("Set-Cookie", "LoggedIn=true#" + login + "; Max-Age=600;  Path=/" + plugin.getRootAlias() + "; Version=\"1\"");
                            // REDIRECT TO MAINPAGE
                            exchange.sendResponseHeaders(200, 0);
                            String cookie = "LoggedIn=true#" + login;
                            cookies.add(cookie);
                            Page page = new Page(webfolder + plugin.getRoot() + "/" + plugin.getIndexPage());
                            HTTPEvent newEvent = new HTTPEvent(webfolder + plugin.getRoot() + "/" + plugin.getIndexPage(), null, null, cookies, true);
                            plugin.handleGetRequest(page, newEvent);
                            responseBody.write(page.line.getBytes());
                        } else {
                            // HANDLE WRONG LOGIN
                            // REDIRECT TO LOGIN
                            exchange.sendResponseHeaders(200, 0);
                            Page page = new Page(webfolder + plugin.getRoot() + "/" + plugin.getLoginPage());
                            HTTPEvent newEvent = new HTTPEvent(webfolder + plugin.getRoot() + "/" + plugin.getLoginPage(), null, null, cookies, true);
                            plugin.handleWrongLogin(page, newEvent);
                            responseBody.write(page.line.getBytes());
                        }
                    }
                }
            } else {
                // READ FILEDATA
                File file = new File(webfolder + plugin.getRoot() + reqFile);
                int fileLength = (int) file.length();
                FileInputStream fileIn = null;
                byte[] fileData = new byte[fileLength];
                try {
                    fileIn = new FileInputStream(file);
                    fileIn.read(fileData);
                } finally {
                    close(fileIn);
                }

                // SEND HEADERS
                responseHeaders.set("Content-Length", String.valueOf(fileLength));
                exchange.sendResponseHeaders(200, fileLength);

                // SEND DATA
                responseBody.write(fileData);
            }
            responseBody.close();
        } else {
            exchange.sendResponseHeaders(200, 0);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(("Error 404 - Plugin '" + root + "' not registered").getBytes());
            responseBody.close();
        }
    }

    // /////////////////////
    //
    // GET CONTENT TYPE
    //
    // /////////////////////
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else if (fileRequested.endsWith(".png")) {
            return "image/png";
        } else if (fileRequested.endsWith(".gif")) {
            return "image/gif";
        } else if (fileRequested.endsWith(".css")) {
            return "text/css";
        } else if (fileRequested.endsWith(".js")) {
            return "text/javascript";
        } else if (fileRequested.endsWith(".xml")) {
            return "text/xml";
        } else if (fileRequested.endsWith(".rtf")) {
            return "text/rtf";
        } else if (fileRequested.endsWith(".bmp") || fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileRequested.endsWith(".tif") || fileRequested.endsWith(".tiff")) {
            return "image/tiff";
        } else if (fileRequested.endsWith(".class") || fileRequested.endsWith(".jar") || fileRequested.endsWith(".bin") || fileRequested.endsWith(".exe") || fileRequested.endsWith(".com") || fileRequested.endsWith(".dll")) {
            return "applicaton/octet-stream";
        } else {
            return "text/plain";
        }
    }

    // /////////////////////
    //
    // CLOSE STREAM
    //
    // /////////////////////
    private void close(Object stream) {
        if (stream == null)
            return;

        try {
            if (stream instanceof Reader) {
                ((Reader) stream).close();
            } else if (stream instanceof Writer) {
                ((Writer) stream).close();
            } else if (stream instanceof InputStream) {
                ((InputStream) stream).close();
            } else if (stream instanceof OutputStream) {
                ((OutputStream) stream).close();
            } else if (stream instanceof Socket) {
                ((Socket) stream).close();
            } else {
                System.err.println("Unable to close object: " + stream);
            }
        } catch (Exception e) {
            System.err.println("Error closing stream: " + e);
        }
    }
}