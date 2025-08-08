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

        TextView helpContent = findViewById(R.id.help_content);

        String helpText = "<h2>Frequently Asked Questions (FAQ)</h2>" +
                "<b>Q: How do I use the reverse vending machine?</b><br>" +
                "A: Simply bring your clean plastic bottles to the machine, log in to your account using the app, and deposit the bottles into the designated slot. The machine will automatically weigh and record your contribution.<br><br>" +
                "<b>Q: How are my points calculated?</b><br>" +
                "A: You earn points based on the weight of the plastic you deposit. You can track your points in the 'Updates' section of the app.<br><br>" +
                "<b>Q: How do I redeem my rewards?</b><br>" +
                "A: Once you have accumulated enough points, you can redeem them for various rewards available in the 'Rewards' section.<br><br>" +
                "<h2>Contact Us</h2>" +
                "If you have any other questions or need further assistance, please feel free to contact our support team at ";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helpContent.setText(Html.fromHtml(helpText, Html.FROM_HTML_MODE_LEGACY));
        }
        helpContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 