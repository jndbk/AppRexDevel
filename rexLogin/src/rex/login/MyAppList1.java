package rex.login;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAppList1 {
	private View myappslist1;
	public MyAppList1(String appName, long timeLastPlayed, String icon, Activity act) {
		setMyappslist(act.getLayoutInflater().inflate(R.layout.myapps1, null));
		TextView tv = (TextView) myappslist1.findViewById(R.id.appName);
		tv.setText(appName);
		if(!icon.contentEquals("na"))
		{
		    ImageView iv = (ImageView) myappslist1.findViewById(R.id.icon1);
		    Drawable d = Drawable.createFromPath(icon);
		    iv.setImageDrawable(d);
		}
		String asString = "";
		if(timeLastPlayed != 0)
		{
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		    asString = formatter.format(timeLastPlayed);
		}
		tv = (TextView) myappslist1.findViewById(R.id.atpd);
		tv.setText(asString);
}

	public View getMyappslist1() {
		return myappslist1;
	}

	public void setMyappslist(View myappslist1) {
		this.myappslist1 = myappslist1;
	}
}
