package wind.instrument.competitions.data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Additional users for item. The owner is set in competition_items
 */
@Entity(name="CompetitionItemUsers")
@Table(name = "competition_item_users", schema = "forumdata")
public class CompetitionItemUsers implements Serializable {
    @Id
    @Column(name =  "competition_item_id")
    private Long competitionItemId;
    @Id
    @Column(name =  "user_id")
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "competition_item_id", referencedColumnName = "competition_item_id", insertable = false, updatable = false)
    private CompetitionItemEntity competitionItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    public Long getCompetitionItemId() {
        return competitionItemId;
    }

    public void setCompetitionItemId(Long competitionItemId) {
        this.competitionItemId = competitionItemId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
