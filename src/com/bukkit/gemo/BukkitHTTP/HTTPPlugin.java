package com.bukkit.gemo.BukkitHTTP;

public class HTTPPlugin {
    private String rootAlias = "";
    private String pluginName = "";
    private String root = "";
    private boolean useAuth = false;
    private boolean own404Page = false;
    private String indexPage = "index.html";
    private String error404Page = "ERROR404.html";
    private String loginPage = "login.html";
    private String checkLoginPage = "checklogin.html";

    public HTTPPlugin(String rootAlias, String pluginName, String root, boolean useAuth) {
        this.rootAlias = rootAlias;
        this.pluginName = pluginName;
        this.root = root;
        this.useAuth = useAuth;
    }

    // /////////////////////
    // HANDLE GET REQUEST
    // /////////////////////
    public void handleGetRequest(Page page, HTTPEvent event) {
    }

    // /////////////////////
    // HANDLE POST REQUEST
    // /////////////////////
    public void handlePostRequest(Page page, HTTPEvent event) {
    }

    // /////////////////////
    // HANDLE 404 PAGE
    // /////////////////////
    public void handle404Page(Page page, HTTPEvent event) {
    }

    // /////////////////////
    // HANDLE WRONG LOGIN
    // /////////////////////
    public void handleWrongLogin(Page page, HTTPEvent event) {
    }

    // /////////////////////
    // HANDLE LOGIN
    // /////////////////////
    public String loginSuccessful(HTTPEvent event) {
        return null;
    }

    /**
     * @return the rootAlias
     */
    public String getRootAlias() {
        return rootAlias;
    }

    /**
     * @param rootAlias
     *            the rootAlias to set
     */
    public void setRootAlias(String rootAlias) {
        this.rootAlias = rootAlias;
    }

    /**
     * @return the pluginName
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * @param pluginName
     *            the pluginName to set
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root
     *            the root to set
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * @return the useAuth
     */
    public boolean isUseAuth() {
        return useAuth;
    }

    /**
     * @param useAuth
     *            the useAuth to set
     */
    public void setUseAuth(boolean useAuth) {
        this.useAuth = useAuth;
    }

    /**
     * @return the own404Page
     */
    public boolean isOwn404Page() {
        return own404Page;
    }

    /**
     * @param own404Page
     *            the own404Page to set
     */
    public void setOwn404Page(boolean own404Page) {
        this.own404Page = own404Page;
    }

    /**
     * @return the indexPage
     */
    public String getIndexPage() {
        return indexPage;
    }

    /**
     * @param indexPage
     *            the indexPage to set
     */
    public void setIndexPage(String indexPage) {
        this.indexPage = indexPage;
    }

    /**
     * @return the error404Page
     */
    public String getError404Page() {
        return error404Page;
    }

    /**
     * @param error404Page
     *            the error404Page to set
     */
    public void setError404Page(String error404Page) {
        this.error404Page = error404Page;
    }

    /**
     * @return the loginPage
     */
    public String getLoginPage() {
        return loginPage;
    }

    /**
     * @param loginPage
     *            the loginPage to set
     */
    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    /**
     * @return the checkLoginPage
     */
    public String getCheckLoginPage() {
        return checkLoginPage;
    }

    /**
     * @param checkLoginPage
     *            the checkLoginPage to set
     */
    public void setCheckLoginPage(String checkLoginPage) {
        this.checkLoginPage = checkLoginPage;
    }
}
