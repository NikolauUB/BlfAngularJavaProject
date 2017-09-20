package wind.instrument.competitions.rest.model;

import java.util.ArrayList;

public class VoteData {
    private long id;
    private ArrayList<Long> userIds; //userIds
    private ArrayList<String> usernames; //userIds
    private String description;
    private String instrmnts;
    private String author;
    private String composition;
    private String videoUrl;
    private String audioUrl;
    private Integer order;

    public VoteData() {

    }

    public VoteData(long id,
                    ArrayList<Long> userIds,
                    ArrayList<String> usernames,
                    String description,
                    String instrmnts,
                    String author,
                    String composition,
                    String videoUrl,
                    String audioUrl) {
        this.setId(id);
        this.setUserIds(userIds);
        this.setUsernames(usernames);
        this.setDescription(description);
        this.setInstrmnts(instrmnts);
        this.setAuthor(author);
        this.setComposition(composition);
        this.setVideoUrl(videoUrl);
        this.setAudioUrl(audioUrl);
    }

    public long getId() {
        return id;
    }


    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Long> userIds) {
        this.userIds = userIds;
    }

    public String getInstrmnts() {
        return instrmnts;
    }

    public void setInstrmnts(String instrmnts) {
        this.instrmnts = instrmnts;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }
}
