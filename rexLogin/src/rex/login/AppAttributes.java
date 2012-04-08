package rex.login;

public class AppAttributes
{
    private String appName;
    private String category = "na";
    private String icon = "na";
    private String packageName;
    public void setAppName(String appName)
    {
        this.appName = appName;
    }
    public String getAppName()
    {
        return appName;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }
    public String getCategory()
    {
        return category;
    }
    public void setIcon(String icon)
    {
        this.icon = icon;
    }
    public String getIcon()
    {
        return icon;
    }
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
    public String getPackageName()
    {
        return packageName;
    }
}
