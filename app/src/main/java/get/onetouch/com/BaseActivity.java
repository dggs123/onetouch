package get.onetouch.com;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by gaurav on 29/8/16.
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    public FirebaseAuth mAuth;

    public void initializeAuthRef()
    {
        mAuth = FirebaseAuth.getInstance();

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    //[To get Current userid]
    public String getUserId()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.d("user", "onAuthStateChanged:signed_in:" + user.getUid());
            return user.getUid();
        }
        else {
            // User is signed out
            Log.d("user", "onAuthStateChanged:signed_out");
            return "Invalid";
        }
    }
    //[ end current userid]
}
