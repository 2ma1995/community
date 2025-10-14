package mini.community.User.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mini.community.User.domain.User;
@Getter
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String name;
    private String email;

    public static UserSummaryDto fromEntity(User user) {
        return new UserSummaryDto(user.getId(), user.getName(), user.getEmail());
    }
}
