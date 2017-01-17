package scalerulerview.zxl.com.scalerulerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import scalerulerview.zxl.com.mylibrary.RulerView;

public class MainActivity extends AppCompatActivity {
    private RulerView mBirthView, mHeightView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruler_layout);
        mBirthView = (RulerView) findViewById(R.id.birthRulerView);
        mBirthView.notifyView();
        mBirthView.setValueChangeListener(new RulerView.OnValueChangeListener() {

            @Override
            public void onValueChange(float value) {

            }
        });
        mHeightView = (RulerView) findViewById(R.id.heightRulerView);
        mHeightView.notifyView();
        mHeightView.setValueChangeListener(new RulerView.OnValueChangeListener() {

            @Override
            public void onValueChange(float value) {

            }
        });
    }
}
