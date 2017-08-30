package fisherdynamic.swarmreporter1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import fisherdynamic.swarmreporter1.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {
    @Bind(R.id.mapImageViewFull) ImageView mapImageViewFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);
        String mapUrl = getIntent().getStringExtra("mapURL");
        Log.d("personal", "mapURL is " + mapUrl);

        if(mapUrl != null){
            Picasso.with(this)
                    .load(mapUrl)
                    .resize(600, 600)
                    .centerCrop()
                    .into(mapImageViewFull);
        }
    }
}
