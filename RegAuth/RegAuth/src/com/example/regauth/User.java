package com.example.regauth;

public class User {

	
	int _id;
	String _login;
	String _password;
	int _admin;
	
	
	public User(String Login, String Password, int Admin)
	{
		this._login = Login;
		this._password = Password;
		this._admin = Admin;
	}
	
	public User(int ID, String Login, String Password, int Admin)
	{
		this._id = ID;
		this._login = Login;
		this._password = Password;
		this._admin = Admin;
	}
	
	public int getID()
	{
		return this._id;
	}
	public void SetID(int ID)
	{
		this._id=ID;
	}
	
	public String getLogin()
	{
		return this._login;
	}
	public void setLogin(String Login)
	{
		this._login=Login;
	}
	
	public String getPassword()
	{
		return this._password;
	}
	public void setPassword(String Password)
	{
		this._password = Password;
	}
	
	public int getAdmin()
	{
		return this._admin;
	}
	public void setAdmin(int Admin)
	{
		this._admin = Admin;
	}

    public String toString() {
        if (this._admin==0){
        	return this._login + " (Пользователь)";
        } else {
        	return this._login + " (Администратор)";
        }
    	
    }
	
}
