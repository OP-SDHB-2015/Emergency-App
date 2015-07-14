package bit.weilytw1.sdhb_client;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by weilytw1 on 3/07/2015.
 */
public class eNotification implements Parcelable
{
    //Class Properties
    String title;
    String description;
    String location;
    Date time;

    public eNotification(String title, String description, String location, Date time)
    {
        this.title = title;
        this.description = description;
        this.location = location;
        this.time = time;
    }

    //Parcelable methods
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
    }

    public String getTitle(){return title;}
    public String getDescription(){return description;}

    public static final Creator<eNotification> CREATOR = new Creator<eNotification>() {
        @Override
        public eNotification createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public eNotification[] newArray(int size) {
            return new eNotification[0];
        }
    };
}
