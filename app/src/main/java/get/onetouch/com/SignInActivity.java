package get.onetouch.com;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Gulzar on 28-08-2016.
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener{
    TextView mAppTitle;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "SignInActivity";
    EditText mEmail,mPassword,mName,mConfirmPassword;
    String email,password,name,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAppTitle= (TextView) findViewById(R.id.AppTitle);
        mName=(EditText) findViewById(R.id.personName);
        mEmail=(EditText) findViewById(R.id.emailId);
        mPassword=(EditText)findViewById(R.id.password);
        mConfirmPassword=(EditText)findViewById(R.id.repassword);
        //Using Custom Font

        Typeface Roboto = Typeface.createFromAsset(getAssets(),"CaviarDreams.ttf");
        mAppTitle.setText("OneTouch");
        mAppTitle.setTypeface(Roboto);
        findViewById(R.id.btn_login).setOnClickListener(this);

        // initalize auth ref
        initializeAuthRef();

        // Firebase for check that user is logged in or not

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    finish();
                    startActivity(new Intent(SignInActivity.this,TapButtonActivity.class));
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // end

    }
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // [Sign up New User]
    void addUser(final String email,final String password)
    {
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                        }
                        hideProgressDialog();
                    }
                });

    }
    // [end Sign Up]

    // [ Sign In Using Email And Password]
    void signInWithEmailAndPassword(final String email,final String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                        }

                        // ...
                    }
                });
    }
    // [end Signin]

    Boolean checkCredntials()
    {
        if(!email.contains("@")) {
            mEmail.setError("Enter Valid Email");
            return false;
        }
        if(!password.equals(confirmPassword) || password.equals("")) {
            mPassword.setError("Password didn't match");
            return false;
        }
        if(name.equals("")) {
            mName.setError("UserName Can't Be Empty");
            return false;
        }
        return true;
    }

        @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login:
                email=mEmail.getText().toString();
                password=mPassword.getText().toString();
                name=mName.getText().toString();
                confirmPassword=mConfirmPassword.getText().toString();
                if(checkCredntials())
                {
                    addUser(email,password);
                }
                break;
        }
    }
}
