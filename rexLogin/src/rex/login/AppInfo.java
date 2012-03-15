package rex.login;

import android.os.Parcel;
import android.os.Parcelable;


public class AppInfo implements Parcelable
{
	// user defined
	private String appname;
	private int count;
	private String category;
	
	// constructors
	public AppInfo(String appname, int count, String category) 
	{
		this.appname = appname;
		this.count = count;
		this.category = category;
	}
	private AppInfo(Parcel parcel){
		this.readFromParcel(parcel);
	}
	
	// getters and setters
	public String getAppname() {
		return appname;
	}
	public void setAppname(String name) {
		this.appname = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String name) {
		this.category = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count)
	{
		this.count = count;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.appname != null){
			sb.append(this.appname).append(' ');
		}
		sb.append('(').append(this.count).append(')');
		sb.append(" [").append(this.category).append(']');
		return sb.toString();
	}
	/**
	 * Any <code>Parcelable</code> needs a static field called CREATOR that
	 * acts as a factory class for the <code>Parcelable</code>.
	 */
	public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {

		public AppInfo createFromParcel(Parcel source) {
			return new AppInfo(source);
		}

		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
	};
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(this.appname);
		parcel.writeInt(this.count);
		parcel.writeString(this.category);
	}
	
	/**
	 * Method for creating a <code>Stock</code> from a <code>Parcelable</code>.
	 * This is not required by the <code>Parcelable</code> interface, you can
	 * instead defer this to <code>Parcelable.Creator</code>'s 
	 * <code>createFromParcel</code> method.
	 * 
	 * @param 	parcel			The <code>Parcelable</code> being used to create
	 * 							a <code>Stock</code> object, presumably this is
	 * 							a <code>Stock</code> object that has been 
	 * 							serialized using the {@link #writeToParcel(Parcel, int) writeToParcel}
	 * 							method
	 */
	public void readFromParcel(Parcel parcel){
		appname = parcel.readString();
		count = parcel.readInt();
		category = parcel.readString();
	}

}
