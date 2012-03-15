package rex.login;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * A data access object for persisting and retrieving stock data. This uses
 * a SQLite database for persistence and retrieval.
 * 
 * @author Michael Galpin
 *
 */
public class AppsDb {
	private static final String TAG = "AppsDb";
	// database metadata
	private static final String DB_NAME = "apps.db";
	private static final int DB_VERSION = 1;
	
	private static final String PTABLE_NAME = "processes";
	private static final String CTABLE_NAME = "categories";
	
	// processes column names
	private static final String APPNAME = "appname";
	private static final String HITS = "hits";
	private static final String CATEGORY = "category";
	
	

	// SQL statements
	private static final String CREATE_PTABLE = "CREATE TABLE " + PTABLE_NAME +
	" ("+APPNAME+" TEXT PRIMARY KEY, "+HITS+" INTEGER)";
	private static final String CREATE_CTABLE = "CREATE TABLE " + CTABLE_NAME +
	" (" + APPNAME + " TEXT PRIMARY KEY, " + CATEGORY + " TEXT)";

	
	private static final String NEWAPPCAT_SQL = "INSERT INTO " + CTABLE_NAME +
	" ("+APPNAME+", "+CATEGORY+") " +
	"VALUES (?,?)";
	private static final String NEWAPP_SQL = "INSERT INTO " + PTABLE_NAME +
	" ("+APPNAME+", "+HITS+") " +
	"VALUES (?,1)";
	private static final String GETAPP_SQL = "SELECT HITS FROM " + PTABLE_NAME +
	" WHERE APPNAME = ?";
	private static final String GETCAT_SQL = "SELECT " + CATEGORY + " FROM " + PTABLE_NAME +
	" WHERE " + APPNAME + " = ?";
	private static final String UPDATEAPP_SQL = "UPDATE " + PTABLE_NAME +
	" SET " + HITS + " = ? WHERE APPNAME = ?";

	// The Context object that created this AppsDb
	private final Context context;
	private final SQLiteOpenHelper helper;
	private  SQLiteStatement stmt;
	private  SQLiteStatement updateStmt;
	private final SQLiteDatabase db;
	private final SQLiteStatement pNewApp;
	private final SQLiteStatement pGetApp;
	private final SQLiteStatement pSetApp;
	

	/**
	 * Constructor that takes a <code>Context</code> object, usually the
	 * <code>Service</code> or <code>Activity</code> that created this
	 * instance. This will initialize the SQLiteOpenHelper used for the
	 * database, and pre-compile the insert and update SQL statements.
	 * 
	 * @param 	ctx			The <code>Context</code> that created this instance
	 */
	public AppsDb(Context ctx){
		context = ctx;
		
		// initialize the database helper
		helper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){
			@Override
			public void onCreate(SQLiteDatabase db) {
				Log.d("AppsDb", "create db");
				try
				{
					db.execSQL(CREATE_PTABLE);
					db.execSQL(CREATE_CTABLE);
				}
				catch(Exception e)
				{
					Log.d("RexLogin", e.getLocalizedMessage());
				}
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, 
					int newVersion) {
				Log.d("AppsDb", "Upgrade");
			}
		};
		
		// open the database
		Log.d("AppsDb", "Starting db");
		db = helper.getWritableDatabase();
		// pre-compile statements
		pNewApp = db.compileStatement(NEWAPP_SQL);
		pGetApp = db.compileStatement(GETAPP_SQL);
		pSetApp = db.compileStatement(UPDATEAPP_SQL);
	}
	public void clearAppInfo()
	{
		db.delete(PTABLE_NAME, null, null);
	}
	public ArrayList<AppInfo> getAppInfo()
	{
		int total = this.getSingleApp("TotalIntervals");
		ArrayList<AppInfo> ret = new ArrayList<AppInfo>();
		Cursor results = db.rawQuery("SELECT appname, hits FROM processes", null);
		int num = results.getCount();
		if(num != 0)
		{
			if (results.moveToFirst())
			{
				int appnameCol = results.getColumnIndex(APPNAME);
				int countCol = results.getColumnIndex(HITS);
				do {
					String appName = results.getString(appnameCol);
					int count = results.getInt(countCol);
					if((appName != null) && 
							((count < total - 1) || 
							 (appName.equals("AppRex")) || 
							 (appName.equals("TotalIntervals"))))
					{
						String cat = this.getCategory(appName);
						if(cat == null)
							cat = "Uncategorized";
						AppInfo info = new AppInfo(appName, 
								count, cat);
						ret.add(info);
					}
				} while (results.moveToNext());
			}
		}
		if (!results.isClosed()){
			results.close();
		}
		return ret;
	}
	public String getCategory(String app)
	{
		String ret = null;
		try
		{
			Cursor results = db.rawQuery("SELECT category FROM categories WHERE appname = '" + app + "'" , null);
			if(results.getCount() != 0)
			{
				results.moveToFirst();
				ret = results.getString(1);
				if (!results.isClosed())
					results.close();
			}
		}
		catch(Exception e)
		{
			Log.e("Exception 1", e.toString());
		}
		
		return ret;
	}
	public void setCategory(String app, String cat)
	{
		try
		{
			db.rawQuery("INSERT INTO categories (" +
					APPNAME + ", " + CATEGORY + ") VALUES(" + app + "," + cat + ")" , null);
		}
		catch(Exception e)
		{
			Log.e("Exception 1", e.toString());
		}
	}
	public int getSingleApp(String app)
	{
		int ret = -1;
		try
		{
			Cursor results = db.rawQuery("SELECT hits FROM processes WHERE appname = '" + app + "'" , null);
			if(results.getCount() != 0)
			{
				results.moveToFirst();
				ret = (int) results.getLong(0);
				if (!results.isClosed())
					results.close();
			}
		}
		catch(Exception e)
		{
			Log.e("Exception 1", e.toString());
		}
		
		return ret;
	}
	public Long incrApp(String app)
	{
		try
		{
			Cursor results = db.rawQuery("SELECT hits FROM processes WHERE appname = '" + app + "'" , null);
			if(results.getCount() != 0)
			{
				results.moveToFirst();
				long hits = results.getLong(0);
//				Log.d(TAG, "App " + app + " hit count: " + Long.toString(hits));
				if (!results.isClosed())
					results.close();
				pSetApp.bindString(2, app);
				pSetApp.bindLong(1, hits + 1);
				pSetApp.execute();
				return (hits + 1);
			}
			else
			{
//				Log.d("Insert", app);
				pNewApp.bindString(1, app);
				pNewApp.executeInsert();
				return 1l;
			}
		}
		catch(Exception e)
		{
			Log.e("Exception 1", e.toString());
		}
		return 0l;
	}
	/**
	 * Method to close the underlying database connection.
	 */
	public void close(){
		helper.close();
	}	
}

