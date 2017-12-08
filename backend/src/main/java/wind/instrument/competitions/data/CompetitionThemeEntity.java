package wind.instrument.competitions.data;

import javax.persistence.*;

@Entity(name = "CompetitionThemeEntity")
@IdClass(CompetitionThemeId.class)
@Table(name = "competition_theme", schema = "forumdata")
public class CompetitionThemeEntity {
    @Id
    @Column(name = "competition_id")
    private Long competitionId;

    @Id
    @Column(name = "theme_id")
    private Long themeId;

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }
}
