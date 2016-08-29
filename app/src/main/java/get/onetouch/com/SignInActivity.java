package get.onetouch.com;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by Gulzar on 28-08-2016.
 */
public class SignInActivity extends AppCompatActivity{
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



    }
}
