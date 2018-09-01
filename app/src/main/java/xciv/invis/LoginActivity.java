package xciv.invis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xciv.invis.Api.ApiService;
import xciv.invis.Api.RetrofitClient;
import xciv.invis.Model.Login;
import xciv.invis.Model.StockOpname;
import xciv.invis.Model.User;
import xciv.invis.SqlLiteConfig.SqlLiteInvis;
import xciv.invis.UtilSession.SessionManager;
import xciv.invis.Utils.Helpers;
import android.util.Log;
import static xciv.invis.R.id.radManual;
import static xciv.invis.R.id.radScan;

public class LoginActivity extends AppCompatActivity {
    EditText Username,Password;
    ImageView btnConfig;
    Button btnLogin;
    RippleView ripLogin;
    private SessionManager sm;
    private String apiUrl,Metode;
    SqlLiteInvis SqlLiteInvis;
    protected Cursor cursor;
    AlertDialog.Builder a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RetrofitClient.Reset();
        sm = new SessionManager(LoginActivity.this);
        Username = (EditText) findViewById(R.id.username);
        Password = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        ripLogin = (RippleView) findViewById(R.id.ripLogin);
        btnConfig = (ImageView) findViewById(R.id.btnConfig);
        SqlLiteInvis = new SqlLiteInvis(this);
        String queryConfig = "SELECT * FROM config_tbl where id='1'";
        SQLiteDatabase database = SqlLiteInvis.getReadableDatabase();
        cursor = database.rawQuery(queryConfig,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            cursor.moveToPosition(0);
            if(cursor.getString(1).toString().equals("")) {
                a = new AlertDialog.Builder(LoginActivity.this);
                a.setCancelable(false);
                a.setIcon(R.mipmap.ic_alert_err);
                a.setTitle("Error Code: 1001");
                a.setMessage("Api URL belum dikonfigurasi");
                a.setPositiveButton("Setting Sekarang",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        Intent goConfig = new Intent(LoginActivity.this,ConfigActivity.class);
                        startActivity(goConfig);
                        finish();
                    }
                });
                AlertDialog dialog = a.create();
                dialog.show();
            }else{
                apiUrl = cursor.getString(1).toString();

                if(cursor.getString(2).toString().equals("Scanner")){
                    Metode = "Scanner";
                }else{
                    Metode = "Manual";
                }
                sm.storeConfig(apiUrl,Metode);
            }
        }
        cursor.close();
        //click btn Login
        ripLogin.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String username = Username.getText().toString();
                String password = Password.getText().toString();
                if(TextUtils.isEmpty(username)){
                    Username.requestFocus();
                    Username.setHintTextColor(R.color.colorRed);
                    Username.setHint("*Username harus diisi");
                }else if(TextUtils.isEmpty(password)){
                    Password.requestFocus();
                    Password.setHintTextColor(R.color.colorRed);
                    Password.setHint("*Password harus diisi");
                }else{
                    try
                    {
                        getTokenServer(username,password);
                    }
                    catch (Exception ex)
                    {
                        Helpers.alertError(1000,ex.getMessage(),LoginActivity.this,false,alert);
                    }

                }
            }
        });

        // click icon config
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goConfig = new Intent(LoginActivity.this,ConfigActivity.class);
                startActivity(goConfig);
                finish();
            }
        });
    }

    public void getTokenServer(final String username,final String password){
        final ApiService Api = RetrofitClient.getApiService(apiUrl);
        Call<Login> call = Api.login(username,password);
        call.enqueue(new Callback<Login>(){
            @Override
            public void onResponse(Call<Login> call, Response<Login> response){
                if(response.isSuccessful()){
                    String user_token = response.body().getToken();
                    sm.storeTokenLogin(user_token);
                    sm.storeUserInfo(username,username);
                    Toast toast = Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Intent goMain = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(goMain);
                    finish();

//                    String authHeader = "Bearer "+user_token;
//
//                    try
//                    {
//                        Call<User> callProfil = Api.getProfile(authHeader);
//                        Response<User> res= callProfil.execute();
//                        if(res.code()!=200)
//                        {
//                            Helpers.alertError(res.code(),res.message(),LoginActivity.this,true,alert);
//                            //sm.logOut();
//                        }
//                        else{
//                            String user_name = res.body().getUserName();
//                            String login_id = res.body().getLoginId();
//                            sm.storeUserInfo(login_id,user_name);
//                            Toast toast = Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                            Intent goMain = new Intent(LoginActivity.this,MainActivity.class);
//                            goMain.putExtra("loginId",login_id);
//                            startActivity(goMain);
//                            finish();
//                        }
//
//                    }
//                    catch (Exception e)
//                    {
//                        String st=Log.getStackTraceString(e);
//                        Helpers.alertError(1000,"Profile tidak ditemukan " + st,LoginActivity.this,false,alert);
//                    }
//
//
                }
                else{
                    switch (response.code()) {
                        case 417:
                        case 401:
                            InputStream ErrBody = response.errorBody().byteStream();
                            ErrBodyOption(ErrBody,response.code(),false);
                            break;
                        case 404:
                            Helpers.alertError(response.code(),"Api Url not found",LoginActivity.this,false,alert);
                            break;
                        case 501:
                            Helpers.alertError(response.code(),"Error internal server",LoginActivity.this,false,alert);
                            break;
                        default://unknown exception
                            Helpers.alertError(response.code(),response.message(),LoginActivity.this,false,alert);
                            break;
                    }
                }
            }
            @Override
            public void onFailure(Call<Login> call, Throwable t){
                Helpers.alertError(1000,""+t,LoginActivity.this,false,alert);
            }
        });

    }
Helpers.IAlert alert=new Helpers.IAlert() {
    @Override
    public void goAct() {
        sm.logOut();
        sm.checkLogin();
        finish();
    }
};

    private void ErrBodyOption(InputStream ErrBody,int ErrCodeResponse,boolean actDialog){
        BufferedReader r = new BufferedReader(new InputStreamReader(ErrBody));
        StringBuilder b = new StringBuilder();
        String err;
        try{
            while((err =r.readLine()) != null){
                b.append(err);
            }
            String jsonInString = b.toString();
            Gson gson = new Gson();
            Login sto = gson.fromJson(jsonInString, Login.class);
            String jsonnResult = sto.getStatus();
            if(actDialog){
                Helpers.alertError(ErrCodeResponse,jsonnResult,LoginActivity.this,true,alert);
            }else{
                Helpers.alertError(ErrCodeResponse,jsonnResult,LoginActivity.this,false,alert);
            }
        }catch (IOException ex){
            Helpers.alertError(ErrCodeResponse,ex.getMessage(),LoginActivity.this,false,alert);
        }
    }


}
