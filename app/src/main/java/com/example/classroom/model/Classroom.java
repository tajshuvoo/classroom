package com.example.classroom.model;

public class Classroom {
    private String classroomId;
    private String className;
    private String section;
    private String room;
    private String subject;
    private String teacherUid;

    public Classroom() {
        // Default constructor required for Firebase
    }

    public Classroom(String classroomId, String className, String section, String room, String subject, String teacherUid) {
        this.classroomId = classroomId;
        this.className = className;
        this.section = section;
        this.room = room;
        this.subject = subject;
        this.teacherUid = teacherUid;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacherUid() {
        return teacherUid;
    }

    public void setTeacherUid(String teacherUid) {
        this.teacherUid = teacherUid;
    }
}
