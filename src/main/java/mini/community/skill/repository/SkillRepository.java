package mini.community.skill.repository;

import mini.community.skill.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    // 여러 스킬 이름을 Skill목록으로 가져옴
    List<Skill> findByNameIn(List<String> names);
    // 하나의 스킬 이름으로 Skill을 찾음
    Optional<Skill> findByName(String name);
}
