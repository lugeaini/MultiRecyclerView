package cn.chenxulu.lib.widget.recycler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

/**
 * @author xulu
 * @date 2017/7/4.
 */
public abstract class BaseExpandAdapter<H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    private static final int GROUP_TYPE = 10001;
    private static final int CHILD_TYPE = 10002;

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    public abstract int getGroupCount();

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    public abstract int getChildrenCount(int groupPosition);

    /**
     * group item is expand
     *
     * @param groupPosition
     * @return
     */
    public abstract boolean isExpand(int groupPosition);

    @Override
    public int getItemCount() {
        int size = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            size++;
            if (isExpand(i)) {
                size += getChildrenCount(i);
            }
        }
        System.out.println("count:" + size);
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        int[] positionArray = getGroupChildPositions(position);
        if (positionArray[1] == -1) {
            return GROUP_TYPE;
        } else {
            return CHILD_TYPE;
        }
    }

    /**
     * array[0]:groupPosition
     * array[1]:childPosition
     *
     * @param position
     * @return
     */
    int[] getGroupChildPositions(int position) {
        int groupPosition = 0;
        int childPosition = 0;

        int size = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            int tempSize = size + 1;
            if (isExpand(i)) {
                tempSize += getChildrenCount(i);
            }
            if (tempSize > position) {
                groupPosition = i;
                childPosition = position - size - 1;
                break;
            }
            size = tempSize;
        }
        System.out.println(position + "," + groupPosition + "," + childPosition);
        return new int[]{groupPosition, childPosition};
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == GROUP_TYPE) {
            return onCreateGroupViewHolder(parent, viewType);
        } else {
            return onCreateChildViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(H holder, final int position) {
        final int[] positionArray = getGroupChildPositions(position);
        if (holder.getItemViewType() == GROUP_TYPE) {
            onBindGroupViewHolder(holder, positionArray[0]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGroupItemClickListener != null) {
                        mGroupItemClickListener.onGroupClick(positionArray[0]);
                    }
                }
            });
        } else {
            onBindChildViewHolder(holder, positionArray[0], positionArray[1]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChildItemClickListener != null) {
                        mChildItemClickListener.onChildClick(positionArray[0], positionArray[1]);
                    }
                }
            });
        }
    }

    /**
     * Create Group ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract H onCreateGroupViewHolder(ViewGroup parent, int viewType);

    /**
     * Create Child ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract H onCreateChildViewHolder(ViewGroup parent, int viewType);

    /**
     * Bind Group ViewHolder
     *
     * @param holder
     * @param groupPosition
     */
    public abstract void onBindGroupViewHolder(H holder, int groupPosition);

    /**
     * Bind Child ViewHolder
     *
     * @param holder
     * @param groupPosition
     * @param childPosition
     */
    public abstract void onBindChildViewHolder(H holder, int groupPosition, int childPosition);

    protected OnChildItemClickListener mChildItemClickListener;
    protected OnGroupItemClickListener mGroupItemClickListener;

    public void setChildItemClickListener(OnChildItemClickListener mChildItemClickListener) {
        this.mChildItemClickListener = mChildItemClickListener;
    }

    public void setGroupItemClickListener(OnGroupItemClickListener mGroupItemClickListener) {
        this.mGroupItemClickListener = mGroupItemClickListener;
    }

    /**
     * Interface definition for a callback to be invoked when a child in this
     * expandable list has been clicked.
     */
    public interface OnChildItemClickListener {
        /**
         * Callback method to be invoked when a child in this expandable list has
         * been clicked.
         *
         * @param groupPosition
         * @param childPosition
         */
        void onChildClick(int groupPosition, int childPosition);
    }

    /**
     * Interface definition for a callback to be invoked when a group in this
     * expandable list has been clicked.
     */
    public interface OnGroupItemClickListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been clicked.
         *
         * @param groupPosition
         */
        void onGroupClick(int groupPosition);
    }
}
