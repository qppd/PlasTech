package com.qppd.plastech;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help Center");

        NestedScrollView scrollView = findViewById(R.id.help_scroll_view);
        MaterialCardView faqCard = findViewById(R.id.faq_card);
        MaterialCardView contactCard = findViewById(R.id.contact_card);

        TextView helpContent = findViewById(R.id.help_content);
        TextView contactInfo = findViewById(R.id.contact_info);
        
        String faqText = "<b><font color='#017d3e'>Q: What is PlasTech Admin Dashboard?</font></b><br>" +
                "A: PlasTech is an administrative application for monitoring and managing Reverse Vending Machines (RVMs). This app is designed exclusively for administrators to oversee RVM operations, not for end users or customers.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I monitor RVM status and performance?</font></b><br>" +
                "A: Use the Monitor section to view real-time data including bin levels, coin stock levels, total plastic weight collected, and historical trends. You can select different dates to analyze performance over time.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I manage my admin profile?</font></b><br>" +
                "A: Update your administrator profile details including name, email, and phone number in the Profile section. View your admin activity logs and account status here.<br><br>" +
                "<b><font color='#017d3e'>Q: What data is available in the Home dashboard?</font></b><br>" +
                "A: The Home section provides an overview of total collections in pesos, recent RVM activities, system notifications, and quick access buttons for administrative tasks.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I analyze RVM data and trends?</font></b><br>" +
                "A: The Updates section provides comprehensive data analysis with daily, weekly, and monthly reports, including charts and statistics about plastic collection efficiency and revenue generation.<br><br>" +
                "<b><font color='#017d3e'>Q: What should I do if an RVM needs maintenance?</font></b><br>" +
                "A: Monitor the status indicators for bin capacity and coin stock levels. When bins are full or coin stock is low, coordinate with field technicians for maintenance. Contact technical support for any system malfunctions.<br><br>" +
                "<b><font color='#017d3e'>Q: How do I access RVM operational logs?</font></b><br>" +
                "A: All RVM transactions and operational data are logged and accessible through the Monitor and Updates sections. Use these logs for performance analysis and troubleshooting.";

        String contactText = "<b><font color='#2b7224'>Administrator Support</font></b><br><br>" +
                "<b>Technical Support:</b> admin-support@plastech.com<br>" +
                "<b>System Admin:</b> +63 XXX XXX XXXX<br>" +
                "<b>Business Hours:</b> 8:00 AM - 5:00 PM (Mon-Fri)<br><br>" +
                "<b>Emergency RVM Support:</b><br>" +
                "For critical RVM malfunctions affecting operations:<br>" +
                "<b>Emergency Hotline:</b> +63 XXX XXX XXXX<br><br>" +
                "<b>Field Technician Dispatch:</b><br>" +
                "For maintenance requests (bin full, coin refill):<br>" +
                "<b>Maintenance Hotline:</b> +63 XXX XXX XXXX";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helpContent.setText(Html.fromHtml(faqText, Html.FROM_HTML_MODE_LEGACY));
            contactInfo.setText(Html.fromHtml(contactText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            helpContent.setText(Html.fromHtml(faqText));
            contactInfo.setText(Html.fromHtml(contactText));
        }

        // Add entry animation for FAQ card
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(faqCard, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}