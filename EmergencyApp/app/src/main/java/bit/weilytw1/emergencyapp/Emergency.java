package bit.weilytw1.emergencyapp;

/**
 * Created by weilytw1 on 2/05/2015.
 */
public class Emergency
{
    String title;
    String description;
    String location;

    public Emergency(String title, String description, String location)
    {
        this.title = title;
        this.description = description;
        this.location = location;
    }

    public void setTitle(String title){this.title = title;}
    public void setDescription(String description){this.description = description;}
    public void setLocation(String location){this.location = location;}

}
