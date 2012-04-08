package rex.login;

import java.util.LinkedList;
import java.util.List;

public class UserInfo
{
    private User currentUser;
    private List<User> friends;
    private static UserInfo theInstance = null;
    
    static UserInfo getUserInfo()
    {
        if(theInstance == null)
            theInstance = new UserInfo();
        return theInstance;
    }

    UserInfo()
    {
        friends = new LinkedList<User>();
    }
    void setUser(User u)
    {
        currentUser = u;
    }
    
    User getUser()
    {
        return currentUser;
    }
    
    void addFriend(User newFriend)
    {
        friends.add(newFriend);
    }
    
    List<User> getFriends()
    {
        return friends;
    }
}
