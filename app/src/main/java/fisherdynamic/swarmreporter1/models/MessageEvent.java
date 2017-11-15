package fisherdynamic.swarmreporter1.models;

/**
 * Created by mf on 11/8/17.
 */

public class MessageEvent {
    public double lat;
    public double lng;

    public MessageEvent(double lat, double lng) {
        this.lat= lat;
        this.lng = lng;
    }
}
