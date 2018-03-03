package edu.asu.plp.logger.model;

public class DatabaseConnection {
    private String Url;
    private String DriverClassName;
    private String DbUsername;
    private String DbPassword;

    public String getDBUrl() { return this.Url; }
    public String getDBDriverClassName() {
        return this.DriverClassName;
    }
    public String getDBUsername() {
        return this.DbUsername;
    }
    public String getDBPassword() {
        return this.DbPassword;
    }

    public void setDBUrl(String Url) {
        this.Url = Url;
    }
    public void setDBDriverClassName(String DriverClassName) {
        this.DriverClassName = DriverClassName;
    }
    public void setDBUsername(String DbUsername) {
        this.DbUsername = DbUsername;
    }
    public void setDBPassword(String DbPassword) {
        this.DbPassword = DbPassword;
    }
}
