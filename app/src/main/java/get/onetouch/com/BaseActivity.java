package get.onetouch.com;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

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

    Public 
}
