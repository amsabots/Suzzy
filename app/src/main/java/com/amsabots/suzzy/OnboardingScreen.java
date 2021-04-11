package com.amsabots.suzzy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsabots.suzzy.ExtrasAdapter.OnboardingAdapter;
import com.amsabots.suzzy.Preferences.OnBoardingScreen;

public class OnboardingScreen extends AppCompatActivity implements View.OnClickListener {
private ViewPager mviewPager;
private LinearLayout mlinearlayout;
OnboardingAdapter onboardingAdapter;
private TextView[] dots;
TextView previous, next,  start_shopping;
private int mcurrentpage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);
        mviewPager = findViewById(R.id.onboarding_viewpager);
        mlinearlayout = findViewById(R.id.Onboarding_bottom_dots);
        onboardingAdapter = new OnboardingAdapter(this);
        mviewPager.setAdapter(onboardingAdapter);
        mDotsInit(0);
        mviewPager.addOnPageChangeListener(viewPagerlistener);
        next = findViewById(R.id.onboarding_next_screen);
        previous = findViewById(R.id.onboarding_previous_screen);
        start_shopping = findViewById(R.id.onboarding_start_shopping);
next.setOnClickListener(this);
previous.setOnClickListener(this);
start_shopping.setOnClickListener(this);

    }
    private void mDotsInit(int position){
dots = new TextView[3];
mlinearlayout.removeAllViews();
for (int i = 0; i < 3; i++){
  dots[i] = new TextView(this);
  dots[i].setText(Html.fromHtml("&#8226"));
  dots[i].setTextSize(35);
  dots[i].setTextColor(getResources().getColor(R.color.colorWhite));
  mlinearlayout.addView(dots[i]);
}
        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorPrimary
            ));
        }
    }
    ViewPager.OnPageChangeListener viewPagerlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
mDotsInit(position);
mcurrentpage = position;
switch (mcurrentpage){
    case 0:
        previous.setVisibility(View.GONE);
        next.setText("Next");
        start_shopping.setVisibility(View.GONE);
        break;
    case 1:
        previous.setVisibility(View.VISIBLE);
        previous.setText("Back");
        next.setText("Next");
        start_shopping.setVisibility(View.GONE);
        break;
    case 2:
        previous.setVisibility(View.VISIBLE);
        previous.setText("Back");
        next.setText("");
        start_shopping.setVisibility(View.VISIBLE);
        break;
}
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.onboarding_next_screen:
            mviewPager.setCurrentItem(mcurrentpage +1);
            break;
        case R.id.onboarding_previous_screen:
            mviewPager.setCurrentItem(mcurrentpage-1);
            break;
        case R.id.onboarding_start_shopping:
            OnBoardingScreen.getInstance(this).setNew(false);
           startActivity(new Intent(OnboardingScreen.this, MainActivity.class).addFlags(
                   Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK
           ));
            break;
    }
    }
}
