package xciv.invis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xciv.invis.Api.ApiService;
import xciv.invis.Api.RetrofitClient;
import xciv.invis.Model.InvAcc;
import xciv.invis.Model.Login;
import xciv.invis.Model.StockOpname;
import xciv.invis.Model.User;
import xciv.invis.SqlLiteConfig.SqlLiteInvis;
import xciv.invis.UtilSession.SessionManager;
import xciv.invis.Utils.Helpers;

public class MainActivity extends AppCompatActivity {
    private SessionManager sm;
    Button btnOk;
    private Spinner spinnerGudang;
    RippleView ripOk;
    ProgressDialog loading;
    Context mContext;
    TextView txtLokasiGudang;
    public String apiUrl,authHeader,Metode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = new SessionManager(MainActivity.this);
        sm.checkLogin();
        mContext = this;
        spinnerGudang = (Spinner) findViewById(R.id.spinnerGudang);
        btnOk = (Button) findViewById(R.id.btnOk);
        ripOk = (RippleView) findViewById(R.id.ripOk);
        txtLokasiGudang = (TextView) findViewById(R.id.txtLokasiGudang);

        HashMap<String, String> map = sm.getTokenLogin();
        String user_token = map.get(sm.KEY_TOKEN);
        authHeader = "Bearer "+user_token;

        HashMap<String, String> mapConfig = sm.getConfig();
        apiUrl = mapConfig.get(sm.KEY_API);
        Metode = mapConfig.get(sm.KEY_METODE);
        getItemSpinner(authHeader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        ripOk.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String ItemSelected = spinnerGudang.getSelectedItem().toString();
                String [] getSplit = ItemSelected.split("-");
                String accId= getSplit[0];
                String accName = getSplit[1];
                if(Metode.equals("Scanner")){
                    Intent goScan = new Intent(MainActivity.this,ScanActivity.class);
                    goScan.putExtra("acc_id",accId);
                    goScan.putExtra("acc_name",accName);
                    startActivity(goScan);
                    finish();
                }else{
                    Intent goManual = new Intent(MainActivity.this,ManualActivity.class);
                    goManual.putExtra("acc_id",accId);
                    goManual.putExtra("acc_name",accName);
                    startActivity(goManual);
                    finish();
                }

            }
        });

    }

    private void getItemSpinner(String authHeader){
        loading = ProgressDialog.show(mContext, null, "Loading...", true, false);
        ApiService Api = RetrofitClient.getApiService(apiUrl);
        Call<List<InvAcc>> call = Api.getInvAcc(authHeader);
        call.enqueue(new Callback<List<InvAcc>>() {
            @Override
            public void onResponse(Call<List<InvAcc>> call, Response<List<InvAcc>> response) {
                if(response.isSuccessful()){
                    loading.dismiss();
                    List<InvAcc> ItemAcc = response.body();
                    List<String> ListSpinner = new ArrayList<String>();
                    for(int i=0; i<ItemAcc.size(); i++){
                        ListSpinner.add(ItemAcc.get(i).getAccId()+"-"+ItemAcc.get(i).getAccName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item,ListSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGudang.setAdapter(adapter);
                    txtLokasiGudang.setHint("");
                }
                else{
                    loading.dismiss();
                    switch (response.code()) {
                        case 417:
                            InputStream ErrBody417 = response.errorBody().byteStream();
                            ErrBodyOption(ErrBody417,response.code(),false);
                            break;
                        case 401:
                            InputStream ErrBody401 = response.errorBody().byteStream();
                            ErrBodyOption(ErrBody401,response.code(),true);
                            break;
                        case 404:
                            Helpers.alertError(response.code(),"Api Url not found",MainActivity.this,false,alert);
                            break;
                        case 501:
                            Helpers.alertError(response.code(),"Error internal server",MainActivity.this,false,alert);
                            break;
                        default://unknown exception
                            Helpers.alertError(response.code(),response.message(),MainActivity.this,false,alert);
                            break;
                    }
                }
            }
            @Override
            public void onFailure(Call<List<InvAcc>> call, Throwable t){
                Helpers.alertError(1000,""+t,MainActivity.this,false,alert);
            }
        });

    }

    Helpers.IAlert alert=new Helpers.IAlert() {
        @Override
        public void goAct() {
            Intent goLogin = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(goLogin);
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
            InvAcc sto = gson.fromJson(jsonInString, InvAcc.class);
            String jsonnResult = sto.getStatus();
            if(actDialog){
                Helpers.alertError(ErrCodeResponse,jsonnResult,MainActivity.this,true,alert);
            }else{
                Helpers.alertError(ErrCodeResponse,jsonnResult,MainActivity.this,false,alert);
            }
        }catch (IOException ex){
            Helpers.alertError(ErrCodeResponse,ex.getMessage(),MainActivity.this,false,alert);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        HashMap<String, String> mapUser = sm.getUserInfo();
        String loginId = mapUser.get(sm.KEY_LOGIN);
        if(loginId!=null){
            menu.add(loginId).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent goConfig = new Intent(MainActivity.this,ConfigActivity.class);
            startActivity(goConfig);
            finish();
            return true;
        }
        else if (id == R.id.action_logout) {
            sm.logOut();
            sm.checkLogin();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
