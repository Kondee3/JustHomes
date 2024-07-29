package me.kondi.JustHomes.Data;

public class DatabaseConfig {

    private String host;
    private String database;

    private String username;
    private String password;
    private String databaseType;
    public DatabaseConfig(String databaseType, String host, String database, String username, String password) {
        this.databaseType = databaseType;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseType() {
        return databaseType;
    }
}
