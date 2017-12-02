package cn.chenxulu.recycler.expand;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.chenxulu.lib.widget.recycler.adapter.BaseExpandAdapter;
import cn.chenxulu.model.ClassItem;
import cn.chenxulu.model.Student;
import cn.chenxulu.widget.recycler.R;

/**
 * @author xulu
 */
public class ExpandActivity extends AppCompatActivity implements BaseExpandAdapter.OnChildItemClickListener, BaseExpandAdapter.OnGroupItemClickListener {
    RecyclerView mRecyclerView;
    ExpandAdapter mAdapter;
    List<ClassItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ClassItem classItem = new ClassItem();
            classItem.setId(i + "");
            classItem.setName("Class" + i);
            ArrayList<Student> studentList = new ArrayList<>();
            classItem.setStudents(studentList);
            int max = new Random().nextInt(10);
            for (int j = 0; j < max; j++) {
                Student student = new Student();
                student.setId(j + "");
                student.setName("Student" + j);
                studentList.add(student);
            }
            mList.add(classItem);
        }

        mAdapter = new ExpandAdapter(this, mList);
        mAdapter.setChildItemClickListener(this);
        mAdapter.setGroupItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onChildClick(int groupPosition, int childPosition) {
        Toast.makeText(this, groupPosition + "," + childPosition, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGroupClick(int groupPosition) {
        Toast.makeText(this, groupPosition + "", Toast.LENGTH_LONG).show();
        ClassItem item = mList.get(groupPosition);
        item.setExpand(!item.isExpand());
        mAdapter.notifyDataSetChanged();
    }
}
