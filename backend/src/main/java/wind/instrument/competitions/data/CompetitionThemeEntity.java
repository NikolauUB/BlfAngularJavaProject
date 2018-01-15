package wind.instrument.competitions.data;

import javax.persistence.*;
import java.util.Collection;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", referencedColumnName = "theme_id", insertable = false, updatable = false)
    private ThemeEntity theme;


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
