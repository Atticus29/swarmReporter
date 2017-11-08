package fisherdynamic.swarmreporter1.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.models.MessageEvent;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.services.LocationService;
import fisherdynamic.swarmreporter1.utilityClasses.Utilities;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import fisherdynamic.swarmreporter1.viewHolders.FirebaseClaimViewHolder;
import io.reactivex.Observable;

public class NewSwarmReportActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = NewSwarmReportActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String size;
    private String accessibility;
    private String description;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private String userName;
    private String userId;
    private Double currenLatitude;
    private Double currentLongitude;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private SwarmReport newSwarmReport = new SwarmReport();
    private DatabaseReference pushRef;
    private SharedPreferences mSharedPreferences;
    private BroadcastReceiver mMessageReceiver;

    @Bind(R.id.reportSwarmButton)
    Button reportSwarmButton;
    @Bind(R.id.baseball)
    RadioButton baseball;
    @Bind(R.id.football)
    RadioButton football;
    @Bind(R.id.basketball)
    RadioButton basketball;
    @Bind(R.id.beachball)
    RadioButton beachball;
    @Bind(R.id.tallLadder)
    RadioButton tallLadder;
    @Bind(R.id.ladder)
    RadioButton ladder;
    @Bind(R.id.reach)
    RadioButton reach;
    @Bind(R.id.hasLadder)
    RadioButton hasLadder;
    @Bind(R.id.addImageButton)
    Button addImageButton;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.descriptionTextView)
    EditText descriptionTextView;
    @Bind(R.id.sizeLabel)
    TextView sizeLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_swarm_report);

        ButterKnife.bind(this);

        reportSwarmButton.setOnClickListener(this);
        addImageButton.setOnClickListener(this);
        sizeLabel.requestFocus();
//        auth = FirebaseAuth.getInstance();

        getSharedPreferences();
        Log.d(TAG, "newSwarm userName is " + userName);
        Log.d(TAG, "newSwarm userId is " + userId);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        currenLatitude = event.lat;
        currentLongitude = event.lng;
        if (userId != null && userName != null && currenLatitude != 0.0 && currentLongitude != 0.0) {
            progressBar.setVisibility(View.GONE);
            reportSwarmButton.setVisibility(View.VISIBLE);
            addImageButton.setVisibility(View.VISIBLE);
        } else {
            Log.d("newSwarm", "either location or user info is null!");
        }
    };

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        String timeString = new SimpleDateFormat("MM/dd/yyyy h:mm a").format(currentTimestamp);
        newSwarmReport.setLatitude(currenLatitude);
        newSwarmReport.setLongitude(currentLongitude);
        newSwarmReport.setReporterName(userName);
        newSwarmReport.setReporterId(userId);
        newSwarmReport.setClaimed(false);
        newSwarmReport.setReportTimestamp(timeString);
        if (newSwarmReport.getImageString() == null) {
            newSwarmReport.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        }

        size = getSize();
        if (size != null) {
            newSwarmReport.setSize(size);
            accessibility = getAccessibility();
            if (accessibility != null) {
                newSwarmReport.setAccessibility(accessibility);
                try {
                    description = getDescription();
                    newSwarmReport.setDescription(description);
                } catch (Exception e) {
                    descriptionTextView.setError("Please add a detailed description");
                    Log.d(TAG, "description is null");
                }
            } else {
                Toast.makeText(NewSwarmReportActivity.this, "Please select accessibility", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(NewSwarmReportActivity.this, "Please select size", Toast.LENGTH_SHORT).show();
        }


        if (size != null && accessibility != null && description != null) {
            database = FirebaseDatabase.getInstance();
            ref = database.getReference("all_unclaimed");
            pushRef = ref.push();
            String pushId = pushRef.getKey();
            newSwarmReport.setReportId(pushId);
        }
        if (v == addImageButton) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        } else if (v == reportSwarmButton) {
            if (newSwarmReport.getSize() != null && newSwarmReport.getAccessibility() != null && newSwarmReport.getDescription() != null && !newSwarmReport.getDescription().equals("")) {
                if (newSwarmReport.getReportId() != null) {
                    pushRef.setValue(newSwarmReport);
                    Utilities.establishSwarmReportInGeoFire(newSwarmReport);
                    Utilities.updateSwarmReportWithItsGeoFireCode(newSwarmReport, userId);
                }
                Intent intent = new Intent(NewSwarmReportActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        newSwarmReport.setImageString(imageEncoded);
    }

    public String getSize() {
        String size = null;
        if (baseball.isChecked()) {
            size = "baseball";
        } else if (football.isChecked()) {
            size = "football";
        } else if (basketball.isChecked()) {
            size = "basketball";
        } else if (beachball.isChecked()) {
            size = "beachball";
        }
        return size;
    }

    public String getDescription() throws Exception {
        String returnVal = null;
        returnVal = descriptionTextView.getText().toString().trim();
        if (returnVal == null || returnVal.equals("")) {
            throw new Exception("Description is null");
        }
        return returnVal;
    }

    public String getAccessibility() {
        String accessibility = null;
        if (reach.isChecked()) {
            accessibility = "within reach";
        } else if (ladder.isChecked()) {
            accessibility = "requires ladder";

        } else if (hasLadder.isChecked()) {
            accessibility = "reporter has ladder";
        } else if (tallLadder.isChecked()) {
            accessibility = "requires tall ladder";
        }
        return accessibility;
    }


    public boolean getSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = mSharedPreferences.getString("userName", null);
        userId = mSharedPreferences.getString("userId", null);
        return true;
    }
}
