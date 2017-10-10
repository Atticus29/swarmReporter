package fisherdynamic.swarmreporter1.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fisherdynamic.swarmreporter1.utilityClasses.Utilities;

import static org.junit.Assert.*;

/**
 * Created by mf on 10/9/17.
 */
public class SwarmReportTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLocationChangesByCreatingSwarmsInSeveralLocations() throws Exception {
        FirebaseDatabase database;
        DatabaseReference ref;
        DatabaseReference pushRef;
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        String timeString = new SimpleDateFormat("MM/dd/yyyy h:mm a").format(currentTimestamp);
        String userName = "testUser";
        String userId = "testUser1234";
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("all_unclaimed");
        String pushId = null;

        SwarmReport s1 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s1.setReporterName(userName);
        s1.setReporterId(userId);
        s1.setClaimed(false);
        s1.setReportTimestamp(timeString);
        s1.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s1.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s1);
        Utilities.updateSwarmReportWithItsGeoFireCode(s1, userId);
        SwarmReport s2 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s3 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s4 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s5 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s6 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s7 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s8 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s9 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
        s9.setReporterName(userName);
        s9.setReporterId(userId);
        s9.setClaimed(false);

        s9.setImageString("https://coxshoney.com/wp-content/uploads/bee_swarm_man.jpg");
        pushRef = ref.push();
        pushId = pushRef.getKey();
        s9.setReportId(pushId);
        Utilities.establishSwarmReportInGeoFire(s9);
        Utilities.updateSwarmReportWithItsGeoFireCode(s9, userId);
        SwarmReport s10 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s11 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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
        SwarmReport s12 = new SwarmReport(-122.4, 42.5, "unitTester", "unitTesterId", "baseball", timeString, "standing", "it's in my front yard");
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