package cn.chenxulu.recycler.headerfooter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.chenxulu.lib.widget.recycler.HeaderFooterRecyclerView;
import cn.chenxulu.widget.recycler.R;

/**
 * @author xulu
 */
public class HeaderFooterActivity extends AppCompatActivity {
    HeaderFooterRecyclerView mRecyclerView;
    List<Integer> mList;
    HeaderFooterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_footer);

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        //mRecyclerView.setLayoutManager(gridLayoutManager);

        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_header_view, mRecyclerView, false);
        mRecyclerView.addHeaderView(headerView);

        View footerView = LayoutInflater.from(this).inflate(R.layout.layout_item_header_view, mRecyclerView, false);
        mRecyclerView.addFooterView(footerView);

        mList = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            mList.add(i);
        }

        mAdapter = new HeaderFooterAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }

}
