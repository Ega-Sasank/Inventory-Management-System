package ineventory.Service.Impl;

import ineventory.Dto.ForgotPasswordRequest;
import ineventory.Dto.LoginRequest;
import ineventory.Dto.ResetPasswordRequest;
import ineventory.Dto.SignupRequest;
import ineventory.Entity.User;
import ineventory.Repository.UserRepository;
import ineventory.Security.JWTUtility;
import ineventory.Service.AuthService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl  {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTUtility jwtUtility;
    private EmailService emailService;
    private ActivityLogService logService;
    private ModelMapper modelMapper;


    ///  SIGN UP
    public String registerUser(SignupRequest request) {

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            return "user already exists";
        }
        User user = modelMapper.map(request,User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logService.log(user,"User Registered");
//        logService.log(user.getUsername(),
//                "User Registered with role: " + user.getRole());

        return "signup successful";
    }

    /// SIGN IN
    public String authenticateUser(LoginRequest loginRequest) {

        User user=userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        if(user==null){
            return "Invalid Username";
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            return "Invalid Password";
        }

        //  IMPORTANT STATUS CHECK
        if(!"ACTIVE".equals(user.getStatus())){
            return "Account waiting for admin approval";
        }

        String token=jwtUtility.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        logService.log(user,"User logged in");
        return token;
    }

    /// FORGOT PASSWORD
    public String processForgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        if(user==null){
            return "Email not Registered";
        }
        String token= UUID.randomUUID().toString();
        user.setResetToken(token);

        userRepository.save(user);
        emailService.sendResetLink(request.getEmail(),token);

        logService.log(user,"Requested password reset");

        return "Password reset link sent";
    }

    /// RESET PASSWORD
    public String resetPassword(ResetPasswordRequest request) {
        User user=userRepository.findByResetToken(request.getToken())
                .orElse(null);
        if(user==null){
            return "Invalid token";
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setResetToken(null);

        userRepository.save(user);
        logService.log(user,"Password reset successful");
        return "Password reset successful";
    }
}
