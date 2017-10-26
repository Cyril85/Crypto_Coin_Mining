package milanroxe.inc.snocoins.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by admin on 07-08-2017.
 */

public class CommonDailogs {

    static AlertDialog.Builder builder;

    public static void showAlertMessage(Context mContext, String strAlertMessage) {


        if (builder == null) {
            builder = new AlertDialog.Builder(mContext);
            builder.setMessage(strAlertMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            builder = null;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
