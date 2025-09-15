package mini.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profile_status")
@Getter
@Setter
@NoArgsConstructor
public class ProfileStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String label;

    @OneToMany(mappedBy = "profileStatus")
    private List<Profile> profiles = new ArrayList<>();
}
