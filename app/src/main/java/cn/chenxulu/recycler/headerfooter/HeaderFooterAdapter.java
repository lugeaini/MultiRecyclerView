package cn.chenxulu.recycler.headerfooter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.chenxulu.widget.recycler.R;

/**
 * Created by xulu on 15/06/2017.
 */

public class HeaderFooterAdapter extends RecyclerView.Adapter<HeaderFooterAdapter.MyViewHolder> {
    private Context mContext;
    private List<Integer> mList;

    public HeaderFooterAdapter(Context mContext, List<Integer> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.titleView = (TextView) view.findViewById(R.id.title_txt);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.titleView.setText(mList.get(position) + "");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
