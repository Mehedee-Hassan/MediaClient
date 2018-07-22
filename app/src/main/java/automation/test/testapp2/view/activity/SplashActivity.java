package automation.test.testapp2.view.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import automation.test.testapp2.R;


public class SplashActivity extends AppCompatActivity {
    private Animation animFadein;
    TextView weplayTitle;
    TextView weplaySubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        initVIew();
        componentAnimation();
        customFontSettings();

        moveFromActivity();

    }

    private void initVIew() {
        weplayTitle = findViewById(R.id.tv_we_play_titile);
        weplaySubTitle = findViewById(R.id.tv_we_play_subtitile);
    }

    private void componentAnimation() {
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        weplayTitle.setVisibility(View.VISIBLE);
        weplayTitle.startAnimation(animFadein);

        weplaySubTitle.setVisibility(View.VISIBLE);
        weplaySubTitle.startAnimation(animFadein);
    }

    private void moveFromActivity() {
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,TabsHeaderActivity.class));
                finish();
//                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);

            }
        }),2000);
    }

    private void customFontSettings() {
        try{
            Typeface typefaceTitle = ResourcesCompat.getFont(this, R.font.finger_paint_regular);
            weplayTitle.setTypeface(typefaceTitle);

            Typeface typefaceSubtitle = ResourcesCompat.getFont(this, R.font.pacifico_regular);
            weplaySubTitle.setTypeface(typefaceSubtitle);


        }catch (Exception ex){

        }
    }
}
