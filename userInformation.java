package userInfo;
public final class userInformation {
    private String url = "jdbc:mysql://localhost:3306/ManageShopOnline";
    private String user = "root";
    private String pass = "Chirag11.";

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }
}
