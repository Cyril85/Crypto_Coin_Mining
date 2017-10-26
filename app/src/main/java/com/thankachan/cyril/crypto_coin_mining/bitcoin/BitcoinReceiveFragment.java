package milanroxe.inc.snocoins.bitcoin;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import milanroxe.inc.snocoins.R;
/**
 * Created by mohan on 25/6/17.
 */

public class BitcoinReceiveFragment extends Fragment {

    private static final String TAG = "bicoin_recive_fragment" ;
    private ImageView qrcode_image;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String bitcoin_address;
    private Bitmap bitmap;
    private FireBaseDBRef fireBaseDBRef;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstance)
    {
        final View view = inflater.inflate(R.layout.bitcoin_receive_fragment,container,false);

        qrcode_image = (ImageView) view.findViewById(R.id.bitcoin_address_qrcode);
        fireBaseDBRef = new FireBaseDBRef();
        databaseReference = fireBaseDBRef.getDatabaseReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String add = sharedPreferences.getString("bitcoin_address","");
        if(!add.equals(""))
            bitcoin_address = add;


        databaseReference.child("bitcoin_address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null) {
                    sharedPreferences.edit().putString("bitcoin_address", dataSnapshot.getValue().toString()).commit();
                    bitcoin_address = dataSnapshot.getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try
        {
            String bal=sharedPreferences.getString("bitcoinbalance","");
            if(bal.equals(""))
               bal = "0";
            bitmap = TextToImageEncode(bitcoin_address+" "+fireBaseDBRef.getFirebaseAuthOfCurrentUser()+" "+bal);

            qrcode_image.setImageBitmap(bitmap);

        }catch (Exception e)
        {
            e.printStackTrace();
        }




        return view;

    }

    private Bitmap TextToImageEncode(String bitcoin_address) {

        BitMatrix bitMatrix;
        try
        {
            bitMatrix = new MultiFormatWriter().encode(
                    bitcoin_address,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    500, 500, null
            );

        }catch (Exception e)
        {
            Log.e(TAG,"Exception");
            return null;

        }

        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
