package fisherdynamic.swarmreporter1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.models.SwarmReport;
import fisherdynamic.swarmreporter1.utilityClasses.Utilities;

public class SwarmPopulationActvitiy extends AppCompatActivity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        if(v == mainLink){
            Intent intent = new Intent(SwarmPopulationActvitiy.this, MainActivity.class);
            startActivity(intent);
        }
    }

    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference pushRef;
    Calendar calendar = Calendar.getInstance();
    java.util.Date now = calendar.getTime();
    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
    String timeString = new SimpleDateFormat("MM/dd/yyyy h:mm a").format(currentTimestamp);
    String userName = "testUser";
    String userId = "testUser1234";

    @Bind(R.id.Main) TextView mainLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swarm_population_actvitiy);
        Log.d("personal", "got into onCreate of SwarmPopulationActivity");
        ButterKnife.bind(this);

        mainLink.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("all_unclaimed");
        String pushId = null;

        SwarmReport s1 = new SwarmReport(45.589701, -122.721515, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s1.setReporterName(userName);
        s1.setReporterId(userId);
        s1.setClaimed(false);
        s1.setReportTimestamp(timeString);
        s1.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        Log.d("personal", "pushId is " + pushId);
        s1.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s1);
        Utilities.updateSwarmReportWithItsGeoFireCode(s1, userId);
        SwarmReport s2 = new SwarmReport(45.590955, -122.719004, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s2.setReporterName(userName);
        s2.setReporterId(userId);
        s2.setClaimed(false);
        s2.setReportTimestamp(timeString);
        s2.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s2.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s2);
        Utilities.updateSwarmReportWithItsGeoFireCode(s2, userId);
        SwarmReport s3 = new SwarmReport(45.590872, -122.724626, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s3.setReporterName(userName);
        s3.setReporterId(userId);
        s3.setClaimed(false);
        s3.setReportTimestamp(timeString);
        s3.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s3.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s3);
        Utilities.updateSwarmReportWithItsGeoFireCode(s3, userId);
        SwarmReport s4 = new SwarmReport(45.592434, -122.721869, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s4.setReporterName(userName);
        s4.setReporterId(userId);
        s4.setClaimed(false);
        s4.setReportTimestamp(timeString);
        s4.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s4.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s4);
        Utilities.updateSwarmReportWithItsGeoFireCode(s4, userId);
        SwarmReport s5 = new SwarmReport(45.983535, -122.708128, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s5.setReporterName(userName);
        s5.setReporterId(userId);
        s5.setClaimed(false);
        s5.setReportTimestamp(timeString);
        s5.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s5.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s5);
        Utilities.updateSwarmReportWithItsGeoFireCode(s5, userId);
        SwarmReport s6 = new SwarmReport(45.621893, -123.450682, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s6.setReporterName(userName);
        s6.setReporterId(userId);
        s6.setClaimed(false);
        s6.setReportTimestamp(timeString);
        s6.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s6.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s6);
        Utilities.updateSwarmReportWithItsGeoFireCode(s6, userId);
        SwarmReport s7 = new SwarmReport(45.617625, -122.053889, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s7.setReporterName(userName);
        s7.setReporterId(userId);
        s7.setClaimed(false);
        s7.setReportTimestamp(timeString);
        s7.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s7.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s7);
        Utilities.updateSwarmReportWithItsGeoFireCode(s7, userId);
        SwarmReport s8 = new SwarmReport(45.232250, -122.685913, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s8.setReporterName(userName);
        s8.setReporterId(userId);
        s8.setClaimed(false);
        s8.setReportTimestamp(timeString);
        s8.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s8.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s8);
        Utilities.updateSwarmReportWithItsGeoFireCode(s8, userId);
        SwarmReport s9 = new SwarmReport(45.700053, -115.836819, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s9.setReporterName(userName);
        s9.setReporterId(userId);
        s9.setClaimed(false);

        s9.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s9.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s9);
        Utilities.updateSwarmReportWithItsGeoFireCode(s9, userId);
        SwarmReport s10 = new SwarmReport(39.641757, -105.784474, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s10.setReporterName(userName);
        s10.setReporterId(userId);
        s10.setClaimed(false);
        s10.setReportTimestamp(timeString);
        s10.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s10.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s10);
        Utilities.updateSwarmReportWithItsGeoFireCode(s10, userId);
        SwarmReport s11 = new SwarmReport(52.532107, -113.689359, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s11.setReporterName(userName);
        s11.setReporterId(userId);
        s11.setClaimed(false);
        s11.setReportTimestamp(timeString);
        s11.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s11.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s11);
        Utilities.updateSwarmReportWithItsGeoFireCode(s11, userId);
        SwarmReport s12 = new SwarmReport(36.287682, 136.843330, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s12.setReporterName(userName);
        s12.setReporterId(userId);
        s12.setClaimed(false);
        s12.setReportTimestamp(timeString);
        s12.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s12.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s12);
        Utilities.updateSwarmReportWithItsGeoFireCode(s12, userId);
    }
}
