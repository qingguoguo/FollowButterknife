package com.qingguoguo.followbutterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.qingguoguo.butterknife.annotations.BindView;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.tv3)
    TextView mTextView1;
    @BindView(R.id.tv4)
    TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
