package ru.cybernut.fiveseconds.model;

public class QuestionSet {

    public enum QuestionSetType {free, paid}

    private String name;
    private String type;
    private String shopItemId;
    private Integer owned;
    private String soundsLink;
    private boolean soundsLoaded;
    private String soundsFilesSize;


    public QuestionSet(String name, String type) {
        this(name, type, false);
    }

    public QuestionSet(String name, String type, boolean soundLoaded) {
        this.name = name;
        this.type = type;
        this.soundsLoaded = soundLoaded;
    }

    public QuestionSet(String name, String type, String soundsLink, boolean soundsLoaded) {
        this(name, type, soundsLoaded);
        this.soundsLink = soundsLink;
    }

    public QuestionSet(String name, String type, String soundsLink, boolean soundsLoaded, String shopItemId, Integer owned, String soundsFilesSize) {
        this(name, type, soundsLink, soundsLoaded);
        this.shopItemId = shopItemId;
        this.owned = owned;
        this.soundsFilesSize = soundsFilesSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShopItemId() {
        return shopItemId;
    }

    public void setShopItemId(String shopItemId) {
        this.shopItemId = shopItemId;
    }

    public Integer getOwned() {
        return owned;
    }

    public void setOwned(Integer owned) {
        this.owned = owned;
    }

    public String getSoundsLink() {
        return soundsLink;
    }

    public void setSoundsLink(String soundsLink) {
        this.soundsLink = soundsLink;
    }

    public boolean isSoundsLoaded() {
        return soundsLoaded;
    }

    public String getSoundsFilesSize() {
        return soundsFilesSize;
    }

    public void setSoundsFilesSize(String soundsFilesSize) {
        this.soundsFilesSize = soundsFilesSize;
    }

    public void setSoundsLoaded(boolean soundsLoaded) {
        this.soundsLoaded = soundsLoaded;
    }
}
