package wind.instrument.competitions.rest.model;

public class VoteData {
    private long id;
    private String username; //username
    private String description;
    private String author;
    private String composition;
    private String videoUrl;
    private String audioUrl;
    private Integer order;

    public VoteData() {

    }

    public VoteData(long id,
                    String username,
                    String description,
                    String author,
                    String composition,
                    String videoUrl,
                    String audioUrl) {
        this.setId(id);
        this.setUserName(username);
        this.setDescription(description);
        this.setAuthor(author);
        this.setComposition(composition);
        this.setVideoUrl(videoUrl);
        this.setAudioUrl(audioUrl);
    }

    public VoteData(long id,
                    String username,
                    String description,
                    String author,
                    String composition,
                    String videoUrl,
                    String audioUrl,
                    Integer order
                    ) {
        this.setId(id);
        this.setUserName(username);
        this.setDescription(description);
        this.setAuthor(author);
        this.setComposition(composition);
        this.setVideoUrl(videoUrl);
        this.setAudioUrl(audioUrl);
        this.setOrder(order);
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return username;
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

    public void setUserName(String username) {
        this.username = username;
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
}
