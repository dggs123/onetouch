package get.onetouch.com;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Gulzar on 29-08-2016.
 */
public class TapButtonActivity extends AppCompatActivity implements View.OnClickListener{
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

        findViewById(R.id.fab).setOnClickListener(this);




    }

    private void RunInBackground() {
        Toast.makeText(TapButtonActivity.this, "Service started", Toast.LENGTH_SHORT).show();
        //BackgroudLocationService is started
        startService(new Intent(getBaseContext(), BackgroundLocationService.class));
        //Toast to display the start service

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                RunInBackground();
                break;
        }
    }
}
