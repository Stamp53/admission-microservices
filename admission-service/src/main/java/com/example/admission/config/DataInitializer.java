package com.example.admission.config;

import com.example.admission.model.AdmissionApplication;
import com.example.admission.model.ApplicationStatus;
import com.example.admission.model.Role;
import com.example.admission.model.User;
import com.example.admission.repository.AdmissionApplicationRepository;
import com.example.admission.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdmissionApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AdmissionApplicationRepository applicationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // 1. Seed Default Administrator
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "System Administrator",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);

            // 2. Seed Approved Student 1
            User student1 = new User("alice", passwordEncoder.encode("student123"), "Alice Smith", Role.ROLE_STUDENT);
            userRepository.save(student1);

            AdmissionApplication app1 = new AdmissionApplication();
            app1.setApplicationNumber("APP-2026-1001");
            app1.setAcademicYear(2026);
            app1.setPermanentRollNumber("ADM-2026-001");
            app1.setStudentName("Alice Smith");
            app1.setEmail("alice@example.com");
            app1.setPhone("9876543210");
            app1.setDateOfBirth(LocalDate.of(2008, 5, 14));
            app1.setGender("Female");
            app1.setAddress("123 Maple Street, Springfield");
            app1.setAppliedClass("Class 11 Science");
            app1.setAssignedClass("Class 11 Science");
            app1.setAssignedSection("Section A");
            app1.setPreviousSchool("St. Xavier High School");
            app1.setMarksPercentage(92.5);
            app1.setStatus(ApplicationStatus.APPROVED);
            app1.setRemarks("Excellent academic background. Admitted directly.");
            app1.setRegisteredBy("STUDENT");
            app1.setAppliedDate(LocalDateTime.now().minusDays(5));
            app1.setDecisionDate(LocalDateTime.now().minusDays(2));
            app1.setUser(student1);
            applicationRepository.save(app1);

            // 3. Seed Approved Student 2 (same class, Section A)
            User student2 = new User("bob", passwordEncoder.encode("student123"), "Bob Jones", Role.ROLE_STUDENT);
            userRepository.save(student2);

            AdmissionApplication app2 = new AdmissionApplication();
            app2.setApplicationNumber("APP-2026-1002");
            app2.setAcademicYear(2026);
            app2.setPermanentRollNumber("ADM-2026-002");
            app2.setStudentName("Bob Jones");
            app2.setEmail("bob@example.com");
            app2.setPhone("9876543211");
            app2.setDateOfBirth(LocalDate.of(2008, 9, 21));
            app2.setGender("Male");
            app2.setAddress("456 Oak Avenue, Springfield");
            app2.setAppliedClass("Class 11 Science");
            app2.setAssignedClass("Class 11 Science");
            app2.setAssignedSection("Section A");
            app2.setPreviousSchool("Central Model School");
            app2.setMarksPercentage(88.0);
            app2.setStatus(ApplicationStatus.APPROVED);
            app2.setRemarks("Admitted to Section A.");
            app2.setRegisteredBy("STUDENT");
            app2.setAppliedDate(LocalDateTime.now().minusDays(4));
            app2.setDecisionDate(LocalDateTime.now().minusDays(2));
            app2.setUser(student2);
            applicationRepository.save(app2);

            // 4. Seed Approved Student 3 (Class 10, Section B)
            User student3 = new User("carol", passwordEncoder.encode("student123"), "Carol White", Role.ROLE_STUDENT);
            userRepository.save(student3);

            AdmissionApplication app3 = new AdmissionApplication();
            app3.setApplicationNumber("APP-2026-1003");
            app3.setAcademicYear(2026);
            app3.setPermanentRollNumber("ADM-2026-003");
            app3.setStudentName("Carol White");
            app3.setEmail("carol@example.com");
            app3.setPhone("9876543212");
            app3.setDateOfBirth(LocalDate.of(2009, 3, 10));
            app3.setGender("Female");
            app3.setAddress("789 Pine Road, Metro City");
            app3.setAppliedClass("Class 10");
            app3.setAssignedClass("Class 10");
            app3.setAssignedSection("Section B");
            app3.setPreviousSchool("Greenwood High");
            app3.setMarksPercentage(85.4);
            app3.setStatus(ApplicationStatus.APPROVED);
            app3.setRemarks("Eligible for Section B.");
            app3.setRegisteredBy("ADMIN");
            app3.setAppliedDate(LocalDateTime.now().minusDays(3));
            app3.setDecisionDate(LocalDateTime.now().minusDays(1));
            app3.setUser(student3);
            applicationRepository.save(app3);

            // 5. Seed Pending Student (self-registered)
            User student4 = new User("david", passwordEncoder.encode("student123"), "David Miller", Role.ROLE_STUDENT);
            userRepository.save(student4);

            AdmissionApplication app4 = new AdmissionApplication();
            app4.setApplicationNumber("APP-2026-1004");
            app4.setAcademicYear(2026);
            app4.setStudentName("David Miller");
            app4.setEmail("david@example.com");
            app4.setPhone("9876543213");
            app4.setDateOfBirth(LocalDate.of(2008, 11, 5));
            app4.setGender("Male");
            app4.setAddress("101 Elm Street, Greenfield");
            app4.setAppliedClass("Class 11 Commerce");
            app4.setPreviousSchool("Lincoln Academy");
            app4.setMarksPercentage(79.0);
            app4.setStatus(ApplicationStatus.PENDING);
            app4.setRegisteredBy("STUDENT");
            app4.setAppliedDate(LocalDateTime.now().minusDays(1));
            app4.setUser(student4);
            applicationRepository.save(app4);

            // 6. Seed Rejected Student
            User student5 = new User("emma", passwordEncoder.encode("student123"), "Emma Watson", Role.ROLE_STUDENT);
            userRepository.save(student5);

            AdmissionApplication app5 = new AdmissionApplication();
            app5.setApplicationNumber("APP-2026-1005");
            app5.setAcademicYear(2026);
            app5.setStudentName("Emma Watson");
            app5.setEmail("emma@example.com");
            app5.setPhone("9876543214");
            app5.setDateOfBirth(LocalDate.of(2008, 4, 15));
            app5.setGender("Female");
            app5.setAddress("202 Cedar Boulevard, Hill Valley");
            app5.setAppliedClass("Class 11 Science");
            app5.setPreviousSchool("Riverdale School");
            app5.setMarksPercentage(52.0);
            app5.setStatus(ApplicationStatus.REJECTED);
            app5.setRemarks("Minimum qualifying cut-off percentage for Science stream is 75%.");
            app5.setRegisteredBy("STUDENT");
            app5.setAppliedDate(LocalDateTime.now().minusDays(6));
            app5.setDecisionDate(LocalDateTime.now().minusDays(3));
            app5.setUser(student5);
            applicationRepository.save(app5);
        }
    }
}
