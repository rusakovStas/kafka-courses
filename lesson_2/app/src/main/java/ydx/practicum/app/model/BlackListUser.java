package ydx.practicum.app.model;

public class BlackListUser {
    private String user;
    private String userToBlock;

    public BlackListUser(String user, String userToBlock) {
        this.user = user;
        this.userToBlock = userToBlock;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserToBlock() {
        return userToBlock;
    }

    public void setUserToBlock(String userToBlock) {
        this.userToBlock = userToBlock;
    }
}
