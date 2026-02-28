package ineventory.Controller;

import ineventory.Dto.ForgotPasswordRequest;
import ineventory.Dto.LoginRequest;
import ineventory.Dto.ResetPasswordRequest;
import ineventory.Dto.SignupRequest;
import ineventory.Entity.Role;
import ineventory.Entity.User;
import ineventory.Repository.UserRepository;
import ineventory.Service.Impl.ActivityLogService;
import ineventory.Service.Impl.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    private final ActivityLogService logService;
    private final UserRepository userRepository;

    ///=====================
    ///  Login Page
    /// =================
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    ///=====================
    ///  signup page
    /// =================
    @GetMapping("/signup")
    public String signupPage(Model model){
        model.addAttribute("signupRequest",new SignupRequest());
        return "auth/signup";
    }

    /// ==================
    /// SIGNUP
    /// ================
    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequest request,Model model){

//        String result=authService.registerUser(request);
//        model.addAttribute("message",result);
//
//        model.addAttribute("loginRequest",new LoginRequest());

            if(request.getRole() == Role.ADMIN){
                request.setStatus("ACTIVE");
                model.addAttribute("message",
                        "Admin registered successfully.");
            }
            else if(request.getRole() == Role.EMPLOYEE){
                request.setStatus("PENDING");
                model.addAttribute("message",
                        "Employee registration submitted. Waiting for admin approval.");
            }

            authService.registerUser(request);

            model.addAttribute("loginRequest", new LoginRequest());

        return "auth/login";
    }

    ///=====================
    ///  Login
    /// =================
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpServletResponse response,
                        Model model){
        String result=authService.authenticateUser(request);
        if(result.equals("Invalid Username")||
        result.equals("Invalid Password")){
            model.addAttribute("message",result);
            model.addAttribute("loginRequest",new LoginRequest());
            return "auth/login";
        }

        // store jwt in HTTP-only cookie
        Cookie cookie = new Cookie("JWT_TOKEN",result);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        // redirect based on role
        // Fetch user only for role-based redirect
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElse(null);
        /*if(user != null && !"ACTIVE".equals(user.getStatus())){
            model.addAttribute("message",
                    "Account waiting for admin approval.");
            model.addAttribute("loginRequest", new LoginRequest());
            return "auth/login";
        }*/

        /*if(user.getRole().name().equals("ADMIN")){
            return "redirect:/admin/dashboard";
        }
        else if(user.getRole().name().equals("MANAGER")){
            return "redirect:/manager/dashboard";
        }
        else {
            return "redirect:/staff/dashboard";
        }*/
        if(user == null){
            return "auth/login";
        }
        if(user.getRole().name().equals("ADMIN")){
            return "redirect:/admin/dashboard";
        }
        else {
            return "redirect:/employee/dashboard";
        }

    }

    /// ==================
    /// FORGOT PASSWORD PAGE
    /// =======================
    @GetMapping("/forgot")
    public String forgotPage() {
        return "auth/forgot";
    }
    ///  FORGOT PASSWORD
    @PostMapping("/forgot")
    public String forgot(@ModelAttribute ForgotPasswordRequest request,
                         Model model) {

        String result = authService.processForgotPassword(request);
        model.addAttribute("message", result);

        model.addAttribute("loginRequest",new LoginRequest());

        return "auth/login";
    }

    /// =========================
    /// RESET PASSWORD PAGE
    /// =========================
    @GetMapping("/reset")
    public String resetPage(@RequestParam String token,Model model){
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        model.addAttribute("resetRequest",request);
        return "auth/reset";
    }
    ///  RESET PASSWORD
    @PostMapping("/reset")
    public String reset(@ModelAttribute ResetPasswordRequest request,
                        Model model) {

        String result = authService.resetPassword(request);
        model.addAttribute("message", result);

        model.addAttribute("loginRequest",new LoginRequest());

        return "auth/login";
    }

    /// logout
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);

        return "redirect:/auth/login";
    }



}
