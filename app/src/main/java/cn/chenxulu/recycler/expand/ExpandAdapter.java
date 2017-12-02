package cn.chenxulu.recycler.expand;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import cn.chenxulu.lib.widget.recycler.adapter.BaseExpandAdapter;
import cn.chenxulu.model.ClassItem;
import cn.chenxulu.model.Student;
import cn.chenxulu.widget.recycler.R;

/**
 * @author xulu
 * @date 2017/7/4.
 */
public class ExpandAdapter extends BaseExpandAdapter {
    private Context mContext;
    private List<ClassItem> mList;

    public ExpandAdapter(Context context, List<ClassItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public boolean isExpand(int groupPosition) {
        return mList.get(groupPosition).isExpand();
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int position) {
        return mList.get(position).getStudents().size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_expend_group_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.textView = (TextView) view.findViewById(R.id.txt);
        return viewHolder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_expend_child_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.textView = (TextView) view.findViewById(R.id.txt);
        return viewHolder;
    }

    @Override
    public void onBindGroupViewHolder(RecyclerView.ViewHolder holder, int groupPosition) {
        ClassItem item = mList.get(groupPosition);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.textView.setText(item.getName());
    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int groupPosition, int childPosition) {
        Student item = mList.get(groupPosition).getStudents().get(childPosition);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.textView.setText(item.getName());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
