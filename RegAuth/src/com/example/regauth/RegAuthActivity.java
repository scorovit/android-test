package com.example.regauth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegAuthActivity extends Activity implements OnClickListener  {
	
	Button btnReg, btnAuth, btnOk, btnNo;
	RelativeLayout relativeLayoutAuth, relativeLayoutBtn;
	EditText edtLogin, edtPassword;
	
	final String LOG_TAG = "myLogs";
	final int REQUEST_CODE = 1;
	DatabaseHelper dbHelper;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
        btnReg = (Button) findViewById(R.id.btnReg);
        btnReg.setOnClickListener(this);

        btnAuth = (Button) findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(this);
        
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
 
        btnNo = (Button) findViewById(R.id.btnNo);
        btnNo.setOnClickListener(this);

        edtLogin = (EditText) findViewById(R.id.edtLogin);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
       
        relativeLayoutBtn = (RelativeLayout) findViewById(R.id.relativeLayoutBtn);
        relativeLayoutAuth = (RelativeLayout) findViewById(R.id.relativeLayoutAuth);
        
        relativeLayoutBtn.setVisibility(View.VISIBLE);
    	relativeLayoutAuth.setVisibility(View.GONE);
    }

	public void onClick(View v) {
		Intent intent;
		   switch (v.getId()) {
		    case R.id.btnAuth:
		        
		        relativeLayoutBtn.setVisibility(View.GONE);
		    	relativeLayoutAuth.setVisibility(View.VISIBLE);
		    break;
		    
		    case R.id.btnReg:
		        intent = new Intent(this, Registration.class);
		        startActivityForResult(intent, REQUEST_CODE);
			break;
			
		    case R.id.btnOk:
				try
				{
					dbHelper=new DatabaseHelper(this);

					String login = edtLogin.getText().toString();
					String password = edtPassword.getText().toString();
					
					Cursor CurUserActive = dbHelper.getUser(login, password);	
					int countUser = CurUserActive.getCount();
					if (countUser==1) {
						CurUserActive.moveToFirst();
						intent = new Intent(this, ViewInform.class);
						intent.putExtra("UserID", CurUserActive.getInt(CurUserActive.getColumnIndex("UserID")));
						intent.putExtra("UserLogin", CurUserActive.getString(CurUserActive.getColumnIndex("UserLogin")));
						intent.putExtra("isAdmin", CurUserActive.getInt(CurUserActive.getColumnIndex("Admin")));
						CurUserActive.close();
						startActivity(intent);
						
					} else {
						Toast.makeText(this, "Неправильно введены данные", Toast.LENGTH_SHORT).show();
					}
					
				}
				catch(Exception ex)
				{
					CatchError(ex.toString(), "Ошибка авторизации: ");
				}


		    break;
		    
		    case R.id.btnNo:
		        relativeLayoutBtn.setVisibility(View.VISIBLE);
		    	relativeLayoutAuth.setVisibility(View.GONE);
		    	edtLogin.setText("");
		    	edtPassword.setText("");
		    break;
		   }
		
	}
	
	void CatchError(String Exception, String message)
	{
		Dialog diag=new Dialog(this);
		diag.setTitle(message);
		TextView txt=new TextView(this);
		txt.setText(Exception);
		diag.setContentView(txt);
		diag.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		String mess;
		
	    if (resultCode == RESULT_OK) {
	        switch (requestCode) {
	        case REQUEST_CODE:
	        	Boolean adduser = data.getBooleanExtra("AddUser", true);
	        	
	        	if (adduser) {
	        		mess = "Добавлен новый пользователь";
	        	} else {
	        		mess = "Отмена операции добавления";
	        	}
        		Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
	        		
	          break;
	        }
	      } else {
	        Toast.makeText(this, "Пользователь не был добавлен", Toast.LENGTH_SHORT).show();
	      }
	}
}