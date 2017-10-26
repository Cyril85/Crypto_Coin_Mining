package milanroxe.inc.snocoins.withdraw;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import milanroxe.inc.snocoins.R;

/**
 * Created by milan sharma on 12-06-2017.
 */

public class TipsAlert extends DialogFragment {
    LayoutInflater layoutInflater;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance)
    {
        layoutInflater = getActivity().getLayoutInflater();
        view = layoutInflater.inflate(R.layout.tips,null);

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Tips");
        alertDialog.setView(view);
        return alertDialog.create();

    }

}
