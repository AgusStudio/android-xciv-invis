package xciv.invis.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import xciv.invis.R;

/**
 * Created by Betha on 8/26/2018.
 */

public  class Helpers {
    public interface IAlert{
        void goAct();
    }
    public static void alertError(int code, String pesan, Context context, final boolean goaction, final IAlert delegate){
        AlertDialog.Builder a;
        a = new AlertDialog.Builder(context);
        a.setCancelable(false);
        a.setIcon(R.mipmap.ic_alert_err);
        a.setTitle("Error Code: "+code);
        a.setMessage(pesan);
        a.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                if(goaction){
                    delegate.goAct();
                }else {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = a.create();
        dialog.show();
    }
}
