package get.onetouch.com;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Gulzar on 28-08-2016.
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
    TextView mAppTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAppTitle= (TextView) findViewById(R.id.AppTitle);


        //Using Custom Font
        Typeface Roboto = Typeface.createFromAsset(getAssets(),"CaviarDreams.ttf");
        mAppTitle.setText("OneTouch");
        mAppTitle.setTypeface(Roboto);
        findViewById(R.id.btn_login).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login:
                startActivity(new Intent(SignInActivity.this,TapButtonActivity.class));
                break;
        }
    }
}
