package ineventory.Dto;

import ineventory.Entity.Role;
import lombok.Data;

@Data
public class SignupRequest {

    private String username;
    private String password;
    private String email;
    private Role role;
    private String status;
}
