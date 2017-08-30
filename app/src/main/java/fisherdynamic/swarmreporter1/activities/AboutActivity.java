package fisherdynamic.swarmreporter1.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fisherdynamic.swarmreporter1.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.demoVideoButton) Button demoVideoButton;
    @Bind(R.id.privacyPolicyLink) TextView privacyPolicyLink;
    @Bind(R.id.contactText) TextView contactText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        demoVideoButton.setOnClickListener(this);
        privacyPolicyLink.setOnClickListener(this);

        contactText.setText(Html.fromHtml("<a href=\"mailto:swarmreport@gmail.com\">Send Feedback</a>"));
        contactText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        if(v == demoVideoButton){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/U55cIMqMMZw")));
            Log.i("Video", "Video Playing....");
        } else if (v == privacyPolicyLink){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://atticus29.github.io/swarmReporterPrivactyPolicy/"));
            startActivity(intent);
        }
    }
}
