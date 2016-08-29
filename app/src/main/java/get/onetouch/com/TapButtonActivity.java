package get.onetouch.com;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Gulzar on 29-08-2016.
 */
public class TapButtonActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView mAppTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapbutton);
        toolbar= (Toolbar) findViewById(R.id.app_bar);
        mAppTitle= (TextView) findViewById(R.id.AppT);
        Typeface Roboto = Typeface.createFromAsset(getAssets(),"CaviarDreams.ttf");
        mAppTitle.setTypeface(Roboto);
        mAppTitle.setTextColor(Color.WHITE);


    }
}
