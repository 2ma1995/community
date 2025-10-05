package mini.community.education.domain;

import jakarta.persistence.*;
import lombok.*;
import mini.community.Profile.entity.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "educations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "school",nullable = false)
    private String school;

    @Column(name = "degree", nullable = false)
    private Integer degree;

    @Column(name = "major")
    private String major;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder
    public Education(Long id, Profile profile, String school, Integer degree, String major, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.profile = profile;
        this.school = school;
        this.degree = degree;
        this.major = major;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setProfile(Profile profile) {
        if (this.profile != null) {
            this.profile.getEducations().remove(this);
        }
        this.profile = profile;

        //무한루프에 빠지지 않도록 체크
        if (!profile.getEducations().contains(this)) {
            profile.getEducations().add(this);
        }
    }
}
