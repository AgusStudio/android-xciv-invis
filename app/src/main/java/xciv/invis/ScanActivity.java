package xciv.invis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import xciv.invis.Model.StockBalance;
import xciv.invis.Model.StockOpname;
import xciv.invis.Model.User;
import xciv.invis.UtilSession.SessionManager;
import xciv.invis.Utils.Helpers;

public class ScanActivity extends AppCompatActivity {
    private SessionManager sm;
    Button btnScan;
    RippleView ripScan;
    AlertDialog.Builder a;
    public String apiUrl,authHeader;
    private String accId,accName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sm = new SessionManager(ScanActivity.this);
        sm.checkLogin();
        btnScan = (Button) findViewById(R.id.btnScan);
        ripScan = (RippleView) findViewById(R.id.ripScan);
        HashMap<String, String> map = sm.getTokenLogin();
        String user_token = map.get(sm.KEY_TOKEN);
        authHeader = "Bearer "+user_token;
        Intent data = getIntent();
        if(data.getExtras() != null){
            accId = data.getExtras().getString("acc_id");
            accName = data.getExtras().getString("acc_name");
        }
        final Activity activity = this;

        ripScan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goMain = new Intent(ScanActivity.this,MainActivity.class);
                startActivity(goMain);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast toast = Toast.makeText(this,"You cancelled the scanning",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else{
                String resultScan = result.getContents();
                getDataStock(accId,resultScan,authHeader);

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getDataStock(final String accId, final String sId, String authHeader)
    {
        ApiService Api = RetrofitClient.getApiService(apiUrl);
        Call<StockBalance> call = Api.getBalance(accId,sId,authHeader);
        call.enqueue(new Callback<StockBalance>() {
            @Override
            public void onResponse(Call<StockBalance> call, Response<StockBalance> response) {
                if(response.isSuccessful()){
                    String stockCode = response.body().getStockCode();
                    String stockName = response.body().getStockName();
                    String unitName = response.body().getUnitName();
                    double qtySize = response.body().getUnitSize();
                    double qtyUnit = response.body().getBalance()/qtySize;
                    double qtyPcs = response.body().getBalance()%qtySize;
                    double floorQtyUnit = Math.floor(qtyUnit);
                    Intent goForm = new Intent(ScanActivity.this,FormActivity.class);
                    goForm.putExtra("acc_id",accId);
                    goForm.putExtra("acc_name",accName);
                    goForm.putExtra("sid",sId);
                    goForm.putExtra("stock_code",stockCode);
                    goForm.putExtra("stock_name",stockName);
                    goForm.putExtra("unit_name",unitName);
                    goForm.putExtra("qty_unit",floorQtyUnit);
                    goForm.putExtra("qty_size",qtySize);
                    goForm.putExtra("qty_pcs",qtyPcs);
                    startActivity(goForm);
                    finish();
                }
                else{
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
                            Helpers.alertError(response.code(),"Api Url not found",ScanActivity.this,false,alert);
                            break;
                        case 501:
                            Helpers.alertError(response.code(),"Error internal server",ScanActivity.this,false,alert);
                            break;
                        default://unknown exception
                            Helpers.alertError(response.code(),response.message(),ScanActivity.this,false,alert);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<StockBalance> call, Throwable t)
            {
                Helpers.alertError(1000,""+t,ScanActivity.this,false,alert);
            }
        });
    }

    Helpers.IAlert alert=new Helpers.IAlert() {
        @Override
        public void goAct() {
            Intent goLogin = new Intent(ScanActivity.this,LoginActivity.class);
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
            StockBalance sto = gson.fromJson(jsonInString, StockBalance.class);
            String jsonnResult = sto.getStatus();
            if(actDialog){
                Helpers.alertError(ErrCodeResponse,jsonnResult,ScanActivity.this,true,alert);
            }else{
                Helpers.alertError(ErrCodeResponse,jsonnResult,ScanActivity.this,false,alert);
            }
        }catch (IOException ex){
            Helpers.alertError(ErrCodeResponse,ex.getMessage(),ScanActivity.this,false,alert);
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
            Intent goConfig = new Intent(ScanActivity.this,ConfigActivity.class);
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
