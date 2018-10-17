package uob_todo.api;

public class UserItem {

    final String name;
    final String bearerToken;

    public UserItem(String name, String bearerToken) {
        this.name = name;
        this.bearerToken = bearerToken;
    }

    public String getName() {
        return name;
    }

    public String getBearerToken() {
        return bearerToken;
    }

}
