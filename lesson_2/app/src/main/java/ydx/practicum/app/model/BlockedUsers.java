package ydx.practicum.app.model;

import java.util.HashSet;
import java.util.Set;

public class BlockedUsers {

    private Set<String> blockedUsers = new HashSet<>();

    public BlockedUsers() {
    }

    public Set<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public void addUser(String userToBlock) {
        this.blockedUsers.add(userToBlock);
    }

    public boolean userIsBlocked(String userToCheck){
        return this.blockedUsers.contains(userToCheck);
    }
}
