package com.example.classroom;

public class ChapterItem {
    private String chapterNum;
    private String chapterName;

    public ChapterItem(String chapterNum, String chapterName) {
        this.chapterNum = chapterNum;
        this.chapterName = chapterName;
    }

    public ChapterItem() {
    }

    public String getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(String chapterNum) {
        this.chapterNum = chapterNum;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}
