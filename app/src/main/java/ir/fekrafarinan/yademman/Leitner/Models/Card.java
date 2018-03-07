package ir.fekrafarinan.yademman.Leitner.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;

import java.io.Serializable;

import ir.fekrafarinan.yademman.Leitner.Utils.ImageDownloader;

public class Card implements Serializable {
    private Bitmap answerImage, questionImage;
    private Uri answerVideo;
    private boolean hasAnswerImage, hasQuestionImage, hasAnswerVideo, hasTest;
    private String ans1, ans2, ans3;
    private int id;
    private String question;
    private String answer;
    private Lesson lesson;

    public Card(final Context context, final int id, String question, String answer, Lesson lesson,
                int hasAnswerImage, int hasQuestionImage,
                boolean hasTest, String ans1, String ans2, String ans3) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.lesson = lesson;
        this.hasAnswerImage = hasAnswerImage == 1;
        this.hasQuestionImage = hasQuestionImage == 1;
        this.hasAnswerVideo = hasAnswerImage == 2;
        this.hasTest = hasTest;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        if (hasQuestionImage()) {
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    questionImage = ImageDownloader.downloadImage(id + "q", context);
                }
            });
        }
        if (hasAnswerImage()) {
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    answerImage = ImageDownloader.downloadImage(id + "a", context);
                }
            });
        }
        if (hasAnswerVideo) {
            answerVideo = Uri.parse("http://leopard.000webhostapp.com/Leitner/database/cardVideos/" + id + "a");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Bitmap getAnswerImage() {
        return answerImage;
    }

    public Bitmap getQuestionImage() {
        return questionImage;
    }

    public boolean hasAnswerImage() {
        return hasAnswerImage;
    }

    public boolean hasQuestionImage() {
        return hasQuestionImage;
    }

    public boolean hasAnswerVideo() {
        return hasAnswerVideo;
    }

    public Uri getAnswerVideo() {
        return answerVideo;
    }

    public boolean isHasTest() {
        return hasTest;
    }

    public void setHasTest(boolean hasTest) {
        this.hasTest = hasTest;
    }

    public String getAns1() {
        return ans1;
    }

    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public void setAns3(String ans3) {
        this.ans3 = ans3;
    }

    public String getAns(int n) {
        switch (n) {
            case 1:
                return ans1;
            case 2:
                return ans2;
            case 3:
                return ans3;
            default:
                return " ";
        }
    }
}
