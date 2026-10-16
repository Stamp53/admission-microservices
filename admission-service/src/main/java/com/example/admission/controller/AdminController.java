package com.example.admission.controller;

import com.example.admission.dto.AdminStudentRegistrationDto;
import com.example.admission.dto.ApprovalDto;
import com.example.admission.dto.RejectionDto;
import com.example.admission.model.AdmissionApplication;
import com.example.admission.model.ApplicationStatus;
import com.example.admission.service.ApplicationService;
import com.example.admission.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public AdminController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalCount", applicationService.countTotal());
        model.addAttribute("pendingCount", applicationService.countPending());
        model.addAttribute("approvedCount", applicationService.countApproved());
        model.addAttribute("rejectedCount", applicationService.countRejected());
        model.addAttribute("recentApplications", applicationService.getAllApplications().stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/applications")
    public String listApplications(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                                   Model model) {
        List<AdmissionApplication> applications;
        if ("PENDING".equalsIgnoreCase(status)) {
            applications = applicationService.getApplicationsByStatus(ApplicationStatus.PENDING);
        } else if ("APPROVED".equalsIgnoreCase(status)) {
            applications = applicationService.getApplicationsByStatus(ApplicationStatus.APPROVED);
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            applications = applicationService.getApplicationsByStatus(ApplicationStatus.REJECTED);
        } else {
            applications = applicationService.getAllApplications();
        }

        model.addAttribute("applications", applications);
        model.addAttribute("currentStatus", status);
        return "admin/applications";
    }

    @GetMapping("/applications/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        AdmissionApplication app = applicationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID: " + id));

        model.addAttribute("admissionApp", app);
        model.addAttribute("application", app);
        String initialClass = app.getAssignedClass() != null ? app.getAssignedClass() : app.getAppliedClass();
        String initialSection = app.getAssignedSection() != null ? app.getAssignedSection() : "AUTO";
        String initialRemarks = app.getRemarks() != null ? app.getRemarks() : "";
        model.addAttribute("approvalDto", new ApprovalDto(initialClass, initialSection, initialRemarks));
        model.addAttribute("rejectionDto", new RejectionDto(""));
        return "admin/application-detail";
    }

    @PostMapping("/applications/{id}/approve")
    public String approveApplication(@PathVariable Long id,
                                     @ModelAttribute("approvalDto") ApprovalDto approvalDto,
                                     RedirectAttributes redirectAttributes) {
        try {
            AdmissionApplication app = applicationService.approveApplication(id, approvalDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application " + app.getApplicationNumber() + " approved / updated! Permanent Roll Number: " + app.getPermanentRollNumber());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/applications/" + id;
    }

    @PostMapping("/applications/{id}/reject")
    public String rejectApplication(@PathVariable Long id,
                                     @ModelAttribute("rejectionDto") RejectionDto rejectionDto,
                                     RedirectAttributes redirectAttributes) {
        try {
            AdmissionApplication app = applicationService.rejectApplication(id, rejectionDto);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Application " + app.getApplicationNumber() + " has been rejected.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/applications/" + id;
    }

    @GetMapping("/register-student")
    public String showAdminRegisterStudentForm(Model model) {
        model.addAttribute("dto", new AdminStudentRegistrationDto());
        return "admin/register-student";
    }

    @PostMapping("/register-student")
    public String handleAdminRegisterStudent(@Valid @ModelAttribute("dto") AdminStudentRegistrationDto dto,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes,
                                             Model model) {
        if (dto.isCreateUserAccount() && dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            if (userService.usernameExists(dto.getUsername().trim())) {
                bindingResult.rejectValue("username", "error.user", "This username is already taken.");
            }
        }

        int currentYear = java.time.Year.now().getValue();
        if (dto.getEmail() != null && dto.getAppliedClass() != null 
                && applicationService.existsByEmailAndClassAndYear(dto.getEmail().trim(), dto.getAppliedClass(), currentYear)) {
            bindingResult.rejectValue("email", "error.duplicate", 
                    "An application for " + dto.getAppliedClass() + " in " + currentYear + " already exists with this email.");
        }

        if (bindingResult.hasErrors()) {
            return "admin/register-student";
        }

        try {
            AdmissionApplication created = applicationService.adminRegisterStudent(dto);
            String msg = "Student registration completed! Assigned Application No: " + created.getApplicationNumber();
            if (created.getPermanentRollNumber() != null) {
                msg += " | Permanent Roll No: " + created.getPermanentRollNumber();
            }
            redirectAttributes.addFlashAttribute("successMessage", msg);
            return "redirect:/admin/applications/" + created.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/register-student";
        }
    }

    @GetMapping("/reports")
    public String classSectionReports(@RequestParam(value = "className", required = false) String className,
                                      @RequestParam(value = "section", required = false) String section,
                                      Model model) {
        List<AdmissionApplication> reportStudents = applicationService.getReport(className, section);
        model.addAttribute("students", reportStudents);
        model.addAttribute("selectedClass", className);
        model.addAttribute("selectedSection", section);
        model.addAttribute("availableClasses", applicationService.getAvailableClasses());
        model.addAttribute("availableSections", applicationService.getAvailableSections());
        model.addAttribute("totalEnrolled", reportStudents.size());
        return "admin/reports";
    }
}
