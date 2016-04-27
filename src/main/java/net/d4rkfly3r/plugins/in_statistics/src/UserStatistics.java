package net.d4rkfly3r.plugins.in_statistics.src;

public class UserStatistics {
    private int kills;
    private String username;

    public UserStatistics incrementKills() {
        kills++;
        return this;
    }

    public UserStatistics decrementKills() {
        kills--;
        return this;
    }

    public int getKills() {
        return kills;
    }

    public UserStatistics setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserStatistics setUsername(String username) {
        this.username = username;
        return this;
    }
}
