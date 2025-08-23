package com.qppd.plastech;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.card.MaterialCardView;
import com.qppd.plastech.Libs.Functionz.FunctionClass;

public class HelpActivity_new extends AppCompatActivity {

    private FunctionClass function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        function = new FunctionClass(this);

        setupToolbar();
        setupContent();
        setupAnimations();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Help Center");
        }
    }

    private void setupContent() {
        TextView helpContent = findViewById(R.id.help_content);
        TextView contactInfo = findViewById(R.id.contact_info);

        String faqText = "<b><font color='#017d3e'>Q: How do I use the Reverse Vending Machine (RVM)?</font></b><br>" +
                "A: Bring your clean plastic bottles to the machine, log in to your account, and deposit the bottles. The machine will weigh and record your contribution. Peso coins will be dispensed based on the weight of the plastic you deposit.<br><br>" +
                "<b><font color='#017d3e'>Q: How does the coin dispensing system work?</font></b><br>" +
                "A: The RVM automatically calculates the value of your plastic waste based on weight and type. Peso coins are dispensed directly from the machine's coin stock.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I monitor RVM status and data?</font></b><br>" +
                "A: Use the Monitor section to view real-time data including bin levels, coin stock, total weight collected, and historical trends. You can select different dates to view historical data.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I manage my admin profile?</font></b><br>" +
                "A: You can update your profile picture, name, email, and phone number in the Profile section. View your activity logs and account status here.<br><br>" +
                "<b><font color='#017d3e'>Q: What information is shown in the Home dashboard?</font></b><br>" +
                "A: The Home section displays your total earnings in pesos, recent recycling activities, daily tips for better recycling, and quick action buttons for easy navigation.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I view data updates and trends?</font></b><br>" +
                "A: The Updates section provides daily, weekly, and monthly data analysis with interactive charts and detailed statistics about plastic collection and earnings.<br><br>" +
                "<b><font color='#017d3e'>Q: What should I do if the RVM is full or malfunctioning?</font></b><br>" +
                "A: The Monitor section will show real-time status alerts. Contact technical support immediately if you notice any issues with bin levels, coin stock, or machine status.";

        String contactText = "<b><font color='#2b7224'>Contact Support</font></b><br><br>" +
                "<b>Email:</b> support@plastech.com<br>" +
                "<b>Phone:</b> +63 XXX XXX XXXX<br>" +
                "<b>Business Hours:</b> 8:00 AM - 5:00 PM (Mon-Fri)<br><br>" +
                "<b>Emergency Technical Support:</b><br>" +
                "For critical RVM issues, contact our 24/7 hotline:<br>" +
                "<b>Emergency Hotline:</b> +63 XXX XXX XXXX";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helpContent.setText(Html.fromHtml(faqText, Html.FROM_HTML_MODE_LEGACY));
            contactInfo.setText(Html.fromHtml(contactText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            helpContent.setText(Html.fromHtml(faqText));
            contactInfo.setText(Html.fromHtml(contactText));
        }
    }

    private void setupAnimations() {
        MaterialCardView headerCard = findViewById(R.id.header_card);
        MaterialCardView faqCard = findViewById(R.id.faq_card);
        MaterialCardView contactCard = findViewById(R.id.contact_card);

        // Staggered card animations
        new Handler().postDelayed(() -> {
            ObjectAnimator fadeInHeader = ObjectAnimator.ofFloat(headerCard, "alpha", 0f, 1f);
            ObjectAnimator slideInHeader = ObjectAnimator.ofFloat(headerCard, "translationY", 50f, 0f);
            AnimatorSet headerSet = new AnimatorSet();
            headerSet.playTogether(fadeInHeader, slideInHeader);
            headerSet.setDuration(400);
            headerSet.start();
        }, 100);

        new Handler().postDelayed(() -> {
            ObjectAnimator fadeInFaq = ObjectAnimator.ofFloat(faqCard, "alpha", 0f, 1f);
            ObjectAnimator slideInFaq = ObjectAnimator.ofFloat(faqCard, "translationY", 50f, 0f);
            AnimatorSet faqSet = new AnimatorSet();
            faqSet.playTogether(fadeInFaq, slideInFaq);
            faqSet.setDuration(400);
            faqSet.start();
        }, 200);

        new Handler().postDelayed(() -> {
            ObjectAnimator fadeInContact = ObjectAnimator.ofFloat(contactCard, "alpha", 0f, 1f);
            ObjectAnimator slideInContact = ObjectAnimator.ofFloat(contactCard, "translationY", 50f, 0f);
            AnimatorSet contactSet = new AnimatorSet();
            contactSet.playTogether(fadeInContact, slideInContact);
            contactSet.setDuration(400);
            contactSet.start();
        }, 300);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
