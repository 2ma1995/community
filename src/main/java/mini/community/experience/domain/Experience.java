package mini.community.experience.domain;

import jakarta.persistence.*;
import lombok.*;
import mini.community.Profile.entity.Profile;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@Table(name = "experiences")
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "description")
    private String description;

    public void setProfile(Profile profile) {
        if (this.profile != null) {
            this.profile.getExperiences().remove(this);
        }
        this.profile = profile;
        if (!profile.getExperiences().contains(this)) {
            profile.getExperiences().add(this);
        }
    }
}
