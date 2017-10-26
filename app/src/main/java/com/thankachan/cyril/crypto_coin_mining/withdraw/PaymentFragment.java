package milanroxe.inc.snocoins.withdraw;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import milanroxe.inc.snocoins.R;

/**
 * Created by milan sharma on 11-06-2017.
 */

public class PaymentFragment extends Fragment {

    private static final String TAG = "PaymentFragment";




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment,container,false);


        return view;
    }
}


