package mini.community.Profile.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ProfileSkillId implements Serializable {
    private Long profile;
    private Long skill;
}
