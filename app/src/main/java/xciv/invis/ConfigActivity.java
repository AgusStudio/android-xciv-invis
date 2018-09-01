package xciv.invis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;

import xciv.invis.SqlLiteConfig.SqlLiteInvis;
import xciv.invis.UtilSession.SessionManager;

public class ConfigActivity extends AppCompatActivity {
    EditText edtSetUrl;
    RadioGroup radGroup;
    RadioButton radScan,radManual,radGroupSelect;
    Button btnSimpan;
    RippleView ripSimpan;
    SqlLiteInvis SqlLiteInvis;
    String Metode;
    protected Cursor cursor;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        sm = new SessionManager(ConfigActivity.this);
        edtSetUrl = (EditText) findViewById(R.id.edtSetURL);
        ripSimpan = (RippleView) findViewById(R.id.ripSimpan);
        radGroup = (RadioGroup) findViewById(R.id.radGroup);
        radManual = (RadioButton) findViewById(R.id.radManual);
        radScan = (RadioButton) findViewById(R.id.radScan);
        btnSimpan = (Button) findViewById(R.id.btnSimpan);
        String queryConfig = "SELECT * FROM config_tbl where id='1'";
        SqlLiteInvis = new SqlLiteInvis(this);
        SQLiteDatabase database = SqlLiteInvis.getReadableDatabase();
        cursor = database.rawQuery(queryConfig,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            cursor.moveToPosition(0);
            edtSetUrl.setText(cursor.getString(1).toString());
            if(cursor.getString(2).toString().equals("Scanner")){
                radScan.setChecked(true);
            }else{
                radManual.setChecked(true);
            }
        }
        cursor.close();
        ripSimpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String SetUrl = edtSetUrl.getText().toString();
                int selectedId = radGroup.getCheckedRadioButtonId();
                if(TextUtils.isEmpty(SetUrl)){
                    edtSetUrl.requestFocus();
                    edtSetUrl.setHintTextColor(R.color.colorRed);
                    edtSetUrl.setHint("*Base Url API harus diisi");
                }else{
                    radGroupSelect = (RadioButton) findViewById(selectedId);
                    Metode = radGroupSelect.getText().toString();
                    SQLiteDatabase database = SqlLiteInvis.getWritableDatabase();
                    database.execSQL(
                            "UPDATE config_tbl set api_url= '"+
                                    SetUrl+"',metode= '"+
                                    Metode+"' where id = '1'"
                    );
                    Toast toast = Toast.makeText(ConfigActivity.this,"Konfigurasi berhasil disimpan ",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    Intent goLogin = new Intent(ConfigActivity.this,LoginActivity.class);
                    startActivity(goLogin);
                    finish();
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goLogin2 = new Intent(ConfigActivity.this,LoginActivity.class);
                startActivity(goLogin2);
                finish();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


}
