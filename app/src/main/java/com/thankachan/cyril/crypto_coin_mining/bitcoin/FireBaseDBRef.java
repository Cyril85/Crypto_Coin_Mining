package milanroxe.inc.snocoins.bitcoin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by milan sharma on 22-06-2017.
 */

public class FireBaseDBRef {

    public FirebaseAuth firebaseAuth;
    public DatabaseReference mDatabaseReference;

    public FireBaseDBRef()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public DatabaseReference getDatabaseReference()
    {
        return mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid());
    }
    public String  getFirebaseAuthOfCurrentUser()
    {
        return firebaseAuth.getCurrentUser().getUid();
    }
}
