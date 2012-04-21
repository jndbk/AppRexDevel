package rex.login;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable
{
    // user defined
    private String packageName;
    private Long startTime;
    private Long stopTime;
    
    // constructors
    public AppInfo(String pname, Long start, Long stop)
    {
        this.startTime = start;
        this.stopTime = stop;
        this.setPackageName(pname);
    }

    private AppInfo(Parcel parcel)
    {
        this.readFromParcel(parcel);
    }

    // getters and setters

    public final Long getStartTime()
    {
        return this.startTime;
    }

    public void setStartTime(Long start)
    {
        startTime = start;
    }

    public final Long getStopTime()
    {
        return this.stopTime;
    }

    public void setStopTime(Long stop)
    {
        stopTime = stop;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (this.packageName != null)
        {
            sb.append(this.packageName).append(' ');
        }
        DateFormat df = DateFormat.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        String startString = df.format(cal.getTime());
        cal.setTimeInMillis(stopTime);
        String stopString = df.format(cal.getTime());
        sb.append(startString).append(":").append(stopString);
        return sb.toString();
    }

    /**
     * Any <code>Parcelable</code> needs a static field called CREATOR that acts
     * as a factory class for the <code>Parcelable</code>.
     */
    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>()
    {

        public AppInfo createFromParcel(Parcel source)
        {
            return new AppInfo(source);
        }

        public AppInfo[] newArray(int size)
        {
            return new AppInfo[size];
        }
    };

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeString(this.packageName);
        parcel.writeLong(this.startTime);
        parcel.writeLong(this.stopTime);
    }

    /**
     * Method for creating a <code>Stock</code> from a <code>Parcelable</code>.
     * This is not required by the <code>Parcelable</code> interface, you can
     * instead defer this to <code>Parcelable.Creator</code>'s
     * <code>createFromParcel</code> method.
     * 
     * @param parcel
     *            The <code>Parcelable</code> being used to create a
     *            <code>Stock</code> object, presumably this is a
     *            <code>Stock</code> object that has been serialized using the
     *            {@link #writeToParcel(Parcel, int) writeToParcel} method
     */
    public void readFromParcel(Parcel parcel)
    {
        this.packageName = parcel.readString();
        startTime = parcel.readLong();
        stopTime = parcel.readLong();
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
