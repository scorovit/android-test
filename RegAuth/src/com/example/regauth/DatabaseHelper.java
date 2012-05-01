package com.example.regauth;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

	static final String dbName="userDB";
	static final String userTable="User";
	static final String colID="UserID";
	static final String colLogin="UserLogin";
	static final String colPassword="UserPassword";
	static final String colAdmin="Admin";
	
	
	public DatabaseHelper(Context context) {
		super(context, dbName, null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Log.d("mytag", "--- onCreate database ---");
		
		db.execSQL("CREATE TABLE "+userTable+" ("+colID+" integer primary key, "+
				colLogin+" text, "+colPassword+" text, "+colAdmin+" integer"+ ");");
		
		Log.d("mytag", "CREATE TABLE "+userTable+" ("+colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				colLogin+" TEXT, "+colPassword+" TEXT, "+colAdmin+" INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+userTable);
		
		onCreate(db);
	}
	
	 void AddUser(User user)
	{
		SQLiteDatabase db= this.getWritableDatabase();
			
		ContentValues cv=new ContentValues();
		
		cv.put(colLogin, user.getLogin());
		cv.put(colPassword, user.getPassword());
		cv.put(colAdmin, user.getAdmin());
		
		long rowID = db.insert(userTable, null, cv);
		Log.d("mytag", "row inserted, ID = " + rowID);
		db.close();
	
	}
	 
	 Cursor getUser(String Login, String Password)
	 {
		SQLiteDatabase db=this.getWritableDatabase();
		//Cursor cur= db.rawQuery("Select * from "+userTable+" where UserLogin='?' and UserPassword='?'", new String[] {Login, Password});
		Cursor cur= db.rawQuery("Select * from "+userTable+" where UserLogin = '" + Login + "'  and UserPassword = '"+Password+"'", null);
		//int count= cur.getCount();
		//cur.close();
		//Boolean result; 
		//if (count>0) result = true; else result = false;
		//return result;
		return cur;
	 }
	 
	 int getUserLoginCount(String Login)
	 {
		SQLiteDatabase db=this.getWritableDatabase();
		Cursor cur= db.rawQuery("Select * from "+userTable+" where UserLogin=?", new String[] {Login});
		int x= cur.getCount();
		cur.close();
		return x;
	 }
	 
	 Cursor getAllUsers()
	 {
		 SQLiteDatabase db=this.getWritableDatabase();
		 Cursor cur= db.rawQuery("SELECT * FROM "+userTable, null);
		 return cur;
	 }
	 
	 
	 public int UpdateUser(User user)
	 {
		 SQLiteDatabase db=this.getWritableDatabase();
		 ContentValues cv=new ContentValues();
		 cv.put(colLogin, user.getLogin());
		 cv.put(colPassword, user.getPassword());
		 cv.put(colAdmin, user.getAdmin());
		 return db.update(userTable, cv, colID+"=?", new String []{String.valueOf(user.getID())});
		 
	 }
	 
	 public void DeleteUser(User user)
	 {
		 SQLiteDatabase db=this.getWritableDatabase();
		 db.delete(userTable,colID+"=?", new String [] {String.valueOf(user.getID())});
		 db.close();
	 }

}
