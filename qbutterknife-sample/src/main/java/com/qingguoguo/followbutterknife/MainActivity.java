package com.qingguoguo.followbutterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qingguoguo.butterknife.ButterKnife;
import com.qingguoguo.butterknife.UnBinder;
import com.qingguoguo.butterknife.annotations.BindView;
import com.qingguoguo.butterknife.annotations.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    TextView mTextView1;
    @BindView(R.id.tv2)
    TextView mTextView2;

    UnBinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        mTextView1.setText("hello android");
        mTextView2.setText("hello Python");
    }

    @OnClick({R.id.tv2, R.id.tv1})
    public void submit(View v) {
        Toast.makeText(this, ((TextView)(findViewById(v.getId()))).getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
