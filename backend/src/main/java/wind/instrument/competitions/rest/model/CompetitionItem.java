package wind.instrument.competitions.rest.model;

public class CompetitionItem {
    private Long id;
    private Long userId;
    private String adUsers;
    private Long compId;
    private String desc;
    private String instrmnts;
    private String author;
    private String composition;
    private String video;
    private String embedVideo;
    private String audio;
    private String embedAudio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompId() {
        return compId;
    }

    public void setCompId(Long compId) {
        this.compId = compId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInstrmnts() {
        return instrmnts;
    }

    public void setInstrmnts(String instrmnts) {
        this.instrmnts = instrmnts;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getEmbedVideo() {
        return embedVideo;
    }

    public void setEmbedVideo(String embedVideo) {
        this.embedVideo = embedVideo;
    }

    public String getEmbedAudio() {
        return embedAudio;
    }

    public void setEmbedAudio(String embedAudio) {
        this.embedAudio = embedAudio;
    }

    public String getAdUsers() {
        return adUsers;
    }

    public void setAdUsers(String adUsers) {
        this.adUsers = adUsers;
    }
}
