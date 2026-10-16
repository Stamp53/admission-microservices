package com.example.admission.controller;

import com.example.admission.dto.StudentRegistrationDto;
import com.example.admission.model.AdmissionApplication;
import com.example.admission.service.ApplicationService;
import com.example.admission.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public AuthController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out securely.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new StudentRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("registrationDto") StudentRegistrationDto dto,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        if (dto.getUsername() != null && userService.usernameExists(dto.getUsername().trim())) {
            bindingResult.rejectValue("username", "error.user", "This username is already in use.");
        }

        int currentYear = java.time.Year.now().getValue();
        if (dto.getEmail() != null && dto.getAppliedClass() != null 
                && applicationService.existsByEmailAndClassAndYear(dto.getEmail().trim(), dto.getAppliedClass(), currentYear)) {
            bindingResult.rejectValue("email", "error.duplicate", 
                    "An application for " + dto.getAppliedClass() + " in " + currentYear + " already exists with this email.");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            AdmissionApplication createdApp = applicationService.registerStudentApplication(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Your Application Number is: " + createdApp.getApplicationNumber()
                    + ". Please login to check your admission status.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/track")
    public String showTrackPage(@RequestParam(value = "appNum", required = false) String appNum, Model model) {
        if (appNum != null && !appNum.trim().isEmpty()) {
            Optional<AdmissionApplication> appOpt = applicationService.findByApplicationNumber(appNum.trim());
            if (appOpt.isPresent()) {
                model.addAttribute("admissionApp", appOpt.get());
                model.addAttribute("application", appOpt.get());
            } else {
                model.addAttribute("errorMessage", "No application found with number: " + appNum);
            }
            model.addAttribute("searchedAppNum", appNum);
        }
        return "track";
    }
}
