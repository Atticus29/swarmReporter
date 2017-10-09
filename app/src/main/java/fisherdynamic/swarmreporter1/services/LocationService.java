package fisherdynamic.swarmreporter1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.Toast;

import fisherdynamic.swarmreporter1.models.SwarmNotification;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SwarmNotification swarmNotification = new SwarmNotification("New swarm", "New swarm", "A new swarm has been reported in your area", this);
    }

//    @Override
//    public int onStartCommand(Intent intent, @IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true) int flags, int startId) {
//        Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Location service destroyed", Toast.LENGTH_SHORT).show();
    }
}
