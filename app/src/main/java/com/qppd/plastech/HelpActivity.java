package com.qppd.plastech;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help Center");

        TextView faqSection = findViewById(R.id.faq_section);
        TextView helpContent = findViewById(R.id.help_content);
        TextView contactSection = findViewById(R.id.contact_section);
        TextView contactInfo = findViewById(R.id.contact_info);

        String faqText = "<b>Q: How do I use the reverse vending machine?</b><br>" +
                "A: Bring your clean plastic bottles to the machine, log in to your account, and deposit the bottles. The machine will weigh and record your contribution.<br><br>" +
                "<b>Q: How are my points calculated?</b><br>" +
                "A: Points are based on the weight of the plastic you deposit. Track your points in the 'Updates' section.<br><br>" +
                "<b>Q: How do I redeem rewards?</b><br>" +
                "A: Redeem points for rewards in the 'Rewards' section.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helpContent.setText(Html.fromHtml(faqText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            helpContent.setText(Html.fromHtml(faqText));
        }
        helpContent.setMovementMethod(LinkMovementMethod.getInstance());

        contactInfo.setText("For further assistance, email us at support@plastech.com");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}