package com.qingguoguo.followbutterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.qingguoguo.butterknife.ButterKnife;
import com.qingguoguo.butterknife.annotations.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    TextView mTextView1;
    @BindView(R.id.tv2)
    TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTextView1.setText("hello android");
        mTextView2.setText("hello Python");
    }
}
