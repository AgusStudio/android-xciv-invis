package xciv.invis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;

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
import xciv.invis.Model.StockOpname;
import xciv.invis.UtilSession.SessionManager;
import xciv.invis.Utils.Helpers;

public class FormActivity extends AppCompatActivity {
    private SessionManager sm;
    private String apiUrl,Metode,authHeader;
    private String accId,accName,sId;
    double qtySize,qtyUnit,qtyPcs;
    TextView txtGudang,txtStock,txtQtyUnit,txtUnitName,txtQtySize,txtUnitName_edt,txtAcuanSize;
    EditText edtQtyUnit,edtQtySize;
    Button btnSimpan;
    RippleView ripSimpan;
    AlertDialog.Builder a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sm = new SessionManager(FormActivity.this);
        sm.checkLogin();

        txtStock = (TextView) findViewById(R.id.txtStock);
        txtGudang = (TextView) findViewById(R.id.txtGudang);
        txtQtyUnit = (TextView) findViewById(R.id.txtQtyUnit);
        txtQtySize = (TextView) findViewById(R.id.txtQtySize);
        txtUnitName = (TextView) findViewById(R.id.txtUnitName);
        txtUnitName_edt = (TextView) findViewById(R.id.txtUnitName_edt);
        txtAcuanSize = (TextView) findViewById(R.id.txtAcuanSize);

        edtQtyUnit = (EditText) findViewById(R.id.edtQtyUnit);
        edtQtySize = (EditText) findViewById(R.id.edtQtySize);
        ripSimpan = (RippleView) findViewById(R.id.ripSimpan);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);

        HashMap<String, String> map = sm.getTokenLogin();
        String user_token = map.get(sm.KEY_TOKEN);
        authHeader = "Bearer "+user_token;

        HashMap<String, String> mapConfig = sm.getConfig();
        apiUrl = mapConfig.get(sm.KEY_API);
        Metode = mapConfig.get(sm.KEY_METODE);

        Intent data = getIntent();
        if(data.getExtras() != null){
            accId = data.getExtras().getString("acc_id");
            accName = data.getExtras().getString("acc_name");
            sId = data.getExtras().getString("sid");
            qtyUnit = data.getExtras().getDouble("qty_unit");
            qtySize = data.getExtras().getDouble("qty_size");
            qtyPcs = data.getExtras().getDouble("qty_pcs");
            final String containStock = data.getExtras().getString("stock_code")+"-"+data.getExtras().getString("stock_name");
            txtStock.setText(containStock);
            txtStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailInfo("Stock",containStock);
                }
            });
            txtGudang.setText(accName);
            txtGudang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailInfo("Gudang",accName);
                }
            });
            final String setUnitName = data.getExtras().getString("unit_name");
            txtUnitName.setText(setUnitName);
            txtUnitName_edt.setText(data.getExtras().getString("unit_name"));
            txtQtyUnit.setText(""+qtyUnit);
            txtQtyUnit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailInfo("Qty Unit",""+qtyUnit+" "+setUnitName);
                }
            });
            txtQtySize.setText(""+qtyPcs);
            txtQtySize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailInfo("Qty Pcs",""+qtyPcs+" Pcs");
                }
            });
            txtAcuanSize.setText("* 1 "+setUnitName+" = "+qtySize+" Pcs");
        }

        ripSimpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String qtyOpnameUnit= edtQtyUnit.getText().toString();
                String qtyOpnamePcs = edtQtySize.getText().toString();
                if(TextUtils.isEmpty(qtyOpnameUnit)){
                    Toast toastUnit = Toast.makeText(FormActivity.this,"Qty unit harus diisi",Toast.LENGTH_LONG);
                    toastUnit.setGravity(Gravity.CENTER, 0, 0);
                    toastUnit.show();
                }else if(TextUtils.isEmpty(qtyOpnamePcs)){
                    Toast toastSize = Toast.makeText(FormActivity.this,"Qty pcs harus diisi",Toast.LENGTH_LONG);
                    toastSize.setGravity(Gravity.CENTER, 0, 0);
                    toastSize.show();
                }else{
                    double valUnit = Double.parseDouble(edtQtyUnit.getText().toString());
                    double valSize = Double.parseDouble(edtQtySize.getText().toString());
                    double qtyOpname = (valUnit*qtySize)+valSize;
                    setStockOpname(accId,sId,qtyOpname,authHeader);
                    goActAsMetode();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goActAsMetode();
            }
        });
    }

Helpers.IAlert alert=new Helpers.IAlert() {
    @Override
    public void goAct() {
        Intent goLogin = new Intent(FormActivity.this,LoginActivity.class);
        startActivity(goLogin);
        finish();
    }
};
    public void goActAsMetode(){
        if(Metode.equals("Scanner")){
            Intent goScan = new Intent(FormActivity.this,ScanActivity.class);
            goScan.putExtra("acc_id",accId);
            goScan.putExtra("acc_name",accName);
            startActivity(goScan);
            finish();
        }else{
            Intent goManual = new Intent(FormActivity.this,ManualActivity.class);
            goManual.putExtra("acc_id",accId);
            goManual.putExtra("acc_name",accName);
            startActivity(goManual);
            finish();
        }
    }

    private void setStockOpname(final String accId, final String sId,double qtyOpname,String authHeader) {
        ApiService Api = RetrofitClient.getApiService(apiUrl);
        Call<StockOpname> call = Api.opname(accId,sId,qtyOpname,authHeader);
        call.enqueue(new Callback<StockOpname>() {
            @Override
            public void onResponse(Call<StockOpname> call, Response<StockOpname> response) {
                if(response.isSuccessful()){
                    Toast toast = Toast.makeText(FormActivity.this,response.body().getStatus(),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    switch (response.code()) {
                        case 401:
                            InputStream ErrBody401 = response.errorBody().byteStream();
                            ErrBodyOption(ErrBody401,response.code(),true);
                            break;
                        case 417:
                            InputStream ErrBody417 = response.errorBody().byteStream();
                            ErrBodyOption(ErrBody417,response.code(),false);
                            break;
                        case 404:
                            Helpers.alertError(response.code(),"Api Url not found",FormActivity.this,false,alert);
                            break;
                        case 501:
                            Helpers.alertError(response.code(),"Error internal server",FormActivity.this,false,alert);
                            break;
                        default://unknown exception
                            Helpers.alertError(response.code(),response.message(),FormActivity.this,false,alert);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<StockOpname> call, Throwable t) {
                Helpers.alertError(1000,""+t,FormActivity.this,false,alert);
            }
        });
    }

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
            StockOpname sto = gson.fromJson(jsonInString, StockOpname.class);
            String jsonnResult = sto.getStatus();
//                                alertError(response.code(),jsonnResult,true);
            if(actDialog){
                Helpers.alertError(ErrCodeResponse,jsonnResult,FormActivity.this,true,alert);
            }else{
                Helpers.alertError(ErrCodeResponse,jsonnResult,FormActivity.this,false,alert);
            }
        }catch (IOException ex){
            Helpers.alertError(ErrCodeResponse,ex.getMessage(),FormActivity.this,false,alert);
//                                alertError(response.code(),ex.getMessage());
        }
    }

    private void detailInfo(String codeInfo,String detail){
        a = new AlertDialog.Builder(FormActivity.this);
        a.setCancelable(false);
        a.setIcon(R.mipmap.ic_info);
        a.setTitle("Info "+codeInfo);
        a.setMessage(detail);
        a.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                dialog.dismiss();
            }
        });
        AlertDialog dialog = a.create();
        dialog.show();
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
            Intent goConfig = new Intent(FormActivity.this,ConfigActivity.class);
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
