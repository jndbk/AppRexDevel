package rex.login;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInstance implements Parcelable
{
    private long startTime;
    private long stopTime;
    public AppInstance(long start, long stop)
    {
        startTime = start;
        stopTime = stop;
    }
    public void setStartTime(long s)
    {
        startTime = s;
    }
    public long getStartTime()
    {
        return startTime;
    }
    public void setStopTime(long s)
    {
        stopTime = s;
    }
    public long getStopTime()
    {
        return stopTime;
    }
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeLong(this.startTime);
        parcel.writeLong(this.stopTime);
    }
    public void readFromParcel(Parcel parcel)
    {
        startTime = parcel.readLong();
        startTime = parcel.readLong();
    }
    private AppInstance(Parcel parcel)
    {
        this.readFromParcel(parcel);
    }

    /**
     * Any <code>Parcelable</code> needs a static field called CREATOR that acts
     * as a factory class for the <code>Parcelable</code>.
     */
    public static final Parcelable.Creator<AppInstance> CREATOR = new Parcelable.Creator<AppInstance>()
    {

        public AppInstance createFromParcel(Parcel source)
        {
            return new AppInstance(source);
        }

        public AppInstance[] newArray(int size)
        {
            return new AppInstance[size];
        }
    };

}
