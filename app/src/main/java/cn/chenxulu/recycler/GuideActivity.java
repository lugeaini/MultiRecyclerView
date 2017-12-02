package cn.chenxulu.recycler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleExpandableListAdapter;

import cn.chenxulu.recycler.expand.ExpandActivity;
import cn.chenxulu.recycler.headerfooter.HeaderFooterActivity;
import cn.chenxulu.recycler.pull.PullRefreshActivity;
import cn.chenxulu.widget.recycler.R;

/**
 * @author xulu
 */
public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }

    public void pull(View v) {
        Intent intent = new Intent(this, PullRefreshActivity.class);
        startActivity(intent);
    }

    public void header(View v) {
        Intent intent = new Intent(this, HeaderFooterActivity.class);
        startActivity(intent);
    }

    public void expand(View v) {
        Intent intent = new Intent(this, ExpandActivity.class);
        startActivity(intent);
    }
}
