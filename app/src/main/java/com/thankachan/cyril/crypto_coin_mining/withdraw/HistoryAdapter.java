package milanroxe.inc.snocoins.withdraw;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import milanroxe.inc.snocoins.R;

/**
 * Created by milan sharma on 11-06-2017.
 */

public class HistoryAdapter extends ArrayAdapter<TransactionModel> {
    public HistoryAdapter(Context context, Vector<TransactionModel> transactions) {
        super(context, R.layout.cust_row, transactions);
        //Toast.makeText(context,transactions.toString(),Toast.LENGTH_SHORT).show();

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Log.e("His","********i  History*******");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.cust_row,parent,false);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView phone = (TextView) view.findViewById(R.id.phone);
        ImageView im=(ImageView) view.findViewById(R.id.imageView2);



        TransactionModel data = getItem(position);
       // String[] user = data.split("\n");

        phone.setText(data.getSno_coins()+"  Blocks"); //blocks
        name.setText(data.getDate());

        Log.e("His","Image link:- "+data.getImagelink());
        if(data.getImagelink().equals("sucess}")) {

            Log.e("His","------In sucess");
            im.setImageDrawable(getDrawable(R.drawable.ic_payment_completed));
        }else if(data.getImagelink().equals("cross}"))
        {
            Log.e("His","------In cros");
            im.setImageDrawable(getDrawable(R.drawable.ic_payment_cancel));
        }

        //phone.setText(user[1]); //phone

        return view;
    }

    private Drawable getDrawable(int i) {
        return getContext().getDrawable(i);
    }


}
