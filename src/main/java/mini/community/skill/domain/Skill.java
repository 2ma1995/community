package mini.community.skill.domain;

import jakarta.persistence.*;
import lombok.*;
import mini.community.Profile.entity.Profile;
import mini.community.Profile.entity.ProfileSkill;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileSkill> profileSkills = new ArrayList<>();
//    @ManyToOne
//    @JoinColumn(name = "profile_id")
//    private Profile profile;
}
