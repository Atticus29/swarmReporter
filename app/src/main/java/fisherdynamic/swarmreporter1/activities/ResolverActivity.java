package fisherdynamic.swarmreporter1.activities;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import fisherdynamic.swarmreporter1.R;
import fisherdynamic.swarmreporter1.services.LocationService;

public class ResolverActivity extends AppCompatActivity {

    public static final String TAG = "ResolverActivity";

    public static final String CONNECT_RESULT_KEY = "connectResult";

    public static final String CONN_STATUS_KEY = "connectionStatus";
    public static final int CONN_SUCCESS = 1;
    public static final int CONN_FAILED  = 2;
    public static final int CONN_CANCELLED = 3;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1111;

    private static final String ERROR_CODE_KEY = "errorCode";
    private static final String DIALOG_FRAG_TAG = "errorDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        // No content needed.
        //setContentView(R.layout.activity_main);

        Intent i = getIntent();

        ConnectionResult result = i.getParcelableExtra(CONNECT_RESULT_KEY);

        if (result.hasResolution()) {
            try {
                Log.i(TAG, "Starting error resolution...");
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent.
                sendStatusToService(CONN_FAILED);
                finish();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            ErrorDialogFragment.newInstance(result.getErrorCode())
                    .show(getSupportFragmentManager(), DIALOG_FRAG_TAG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult(): Connection problem resolved");
                sendStatusToService(CONN_SUCCESS);
            } else {
                sendStatusToService(CONN_CANCELLED);
                Log.w(TAG, "onActivityResult(): Resolution cancelled");
            }
            // Nothing more to do in this activity
            finish();
        }
    }

    private void sendStatusToService(int status) {
        Intent i = new Intent(this, LocationService.class);
        i.putExtra(CONN_STATUS_KEY, status);
        startService(i);
    }

    // Fragment to display an error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        public static ErrorDialogFragment newInstance(int errorCode) {
            ErrorDialogFragment f = new ErrorDialogFragment();
            // Pass the error that should be displayed
            Bundle args = new Bundle();
            args.putInt(ERROR_CODE_KEY, errorCode);
            f.setArguments(args);
            return f;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = getArguments().getInt(ERROR_CODE_KEY);
            return GooglePlayServicesUtil.getErrorDialog(
                    errorCode, getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            Log.i(TAG, "Dialog dismissed");
        }
    }
}
