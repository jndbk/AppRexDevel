package rex.login;

public class User
{
    private String name;
    private String id;
    
    public User(String myName, String myId)
    {
        name = myName;
        id = myId;
    }

    public void setName(String n)
    {
        name = n;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setId(String i)
    {
        id = i;
    }
    
    public String getId()
    {
        return id;
    }
}
