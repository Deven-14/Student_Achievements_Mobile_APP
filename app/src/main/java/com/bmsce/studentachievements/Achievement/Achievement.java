package com.bmsce.studentachievements.Achievement;

import org.json.JSONException;
import org.json.JSONObject;

public class Achievement {

    private String nameOfEvent, detailsOfEvent, award, level, certificate, yearOfAchievement;

    public Achievement(JSONObject achievement) throws JSONException {
        this.nameOfEvent = achievement.getString("nameOfEvent");
        this.detailsOfEvent = achievement.getString("detailsOfEvent");
        this.award = achievement.getString("award");
        this.level = achievement.getString("level");
        this.certificate = achievement.getString("certificate");
        this.yearOfAchievement = achievement.getString("yearOfAchievement");
    }

    public String getAward() {
        return award;
    }

    public String getLevel() {
        return level;
    }

    public String getYearOfAchievement() {
        return yearOfAchievement;
    }

    public String getNameOfEvent() {
        return nameOfEvent;
    }

    public String getDetailsOfEvent() {
        return detailsOfEvent;
    }

    public String getCertificate() {
        return certificate;
    }
}
