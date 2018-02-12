package cn.chenxulu.recycler.pull;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.chenxulu.lib.widget.recycler.PullRefreshRecyclerView;
import cn.chenxulu.widget.recycler.R;

/**
 * @author xulu
 */
public class PullRefreshActivity extends AppCompatActivity implements PullRefreshRecyclerView.LoadingListener {
    PullRefreshRecyclerView mRecyclerView;
    List<Integer> mList;
    PullRefreshAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_refresh_load_more);

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        //mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setLoadingListener(this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_item_header_view, mRecyclerView, false);
        mRecyclerView.addHeaderView(headerView);

        mList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            mList.add(i);
        }
        mRecyclerView.setLoadMoreEnabled(true);

        mAdapter = new PullRefreshAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.refreshComplete();

                mList.clear();
                for (int i = 1; i <= 10; i++) {
                    mList.add(i);
                }
                mRecyclerView.setLoadMoreEnabled(true);

                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.loadMoreComplete();

                int size = mList.size();
                for (int i = 1; i <= 10; i++) {
                    mList.add(i + size);
                }
                mRecyclerView.setLoadMoreEnabled(mList.size() < 40);

                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }
}
