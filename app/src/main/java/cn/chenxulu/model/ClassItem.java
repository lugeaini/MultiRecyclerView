package cn.chenxulu.model;

import java.util.ArrayList;

/**
 *
 * @author xulu
 * @date 16/5/26.
 */
public class ClassItem {
    private String id;
    private String name;
    private ArrayList<Student> students;
    private boolean isExpand;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
