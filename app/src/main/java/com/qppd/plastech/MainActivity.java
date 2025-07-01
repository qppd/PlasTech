package com.qppd.plastech;
import android.os.Bundle;
import android.content.Intent;
import com.daimajia.androidanimations.library.Techniques;
import com.qppd.plastech.Libs.IntentManager.IntentManagerClass;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;
import java.util.Objects;

public class MainActivity extends AwesomeSplash {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.white);
        configSplash.setAnimCircularRevealDuration(250);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);
        configSplash.setTitleSplash("PlasTech");
        configSplash.setTitleTextColor(R.color.black);
        configSplash.setLogoSplash(R.drawable.applogo_no_background);
        configSplash.setAnimLogoSplashDuration(3000);
        configSplash.setAnimLogoSplashTechnique(Techniques.BounceInUp);
    }

    @Override
    public void animationsFinished() {
        IntentManagerClass.intentsify(MainActivity.this, LoginActivity.class,
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
    }
}