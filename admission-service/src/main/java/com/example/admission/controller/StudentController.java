package com.example.admission.controller;

import com.example.admission.model.AdmissionApplication;
import com.example.admission.model.User;
import com.example.admission.service.ApplicationService;
import com.example.admission.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public StudentController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return "redirect:/login?logout=true";
        }
        User user = userOpt.get();
        model.addAttribute("user", user);
        Optional<AdmissionApplication> appOpt = applicationService.getApplicationForUser(user);
        model.addAttribute("allApplications", applicationService.getAllApplicationsForUser(user));
        if (appOpt.isPresent()) {
            AdmissionApplication app = appOpt.get();
            model.addAttribute("admissionApp", app);
            model.addAttribute("hasApplication", true);
        } else {
            model.addAttribute("hasApplication", false);
        }
        return "student/dashboard";
    }
}
