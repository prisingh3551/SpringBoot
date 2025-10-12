package com.priyasingh.FirstSpring;


import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public HelloResponse helloGet() {

        return new HelloResponse("Hello World!");
    }

    @GetMapping("/hello/{name}")
    public HelloResponse helloGet(@PathVariable String name) {

        return new HelloResponse("Hello, " + name + ".");
    }

    @PostMapping("/hello")
    public HelloResponse helloPost(@RequestBody String name) {
        return new HelloResponse("Hello, " + name + "!");
    }
}
