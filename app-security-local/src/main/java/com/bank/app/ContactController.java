package com.bank.app;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String getContacts() {
        return "Returning all contacts";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String addContact() {
        return "New contact added!";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteContact(@PathVariable int id) {
        return "Contact " + id + " deleted!";
    }

    @GetMapping("/public/info")
    public String publicInfo() {
        return "This is a public endpoint";
    }
}
