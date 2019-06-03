package com.example.demo.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
     UserService userService;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "/registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
//        model.addAttribute("user",user);
        if(result.hasErrors()) {
            return "registration";
        }
        else {
            user.setEnabled(true);
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        return "redirect:/";
    }

//    @RequestMapping("/login")
//    public String index() {
//        return "index";
//    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/")
    public String listUser(Model model){
        model.addAttribute("users", userRepository.findAll());
        if(userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "userlist";
    }


    @GetMapping("/adduser")
    public String UserForm(Model model) {
        model.addAttribute("users",userRepository.findAll());
        model.addAttribute("user", new User());
        return "userform";
    }

    @PostMapping("/processuser")
    public String processForm(@Valid User user, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("users", userRepository.findAll());
            return "userform";
        }

//    course.setUser(userService.getUser());
        userRepository.save(user);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showUser(@PathVariable("id") long id, Model model){
        model.addAttribute("user", userRepository.findById(id).get());
        return "courselist";
    }

    @RequestMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, Model model){
        model.addAttribute("messages", messageRepository.findAll());
        model.addAttribute("user",userRepository.findById(id).get());
        return "userform";
    }

    @RequestMapping("/delete/{id}")
    public String delUser(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return "redirect:/";
    }



    @RequestMapping("/addmessage/{id}")
    public String addMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());

        return "redirect:/addmessages";
    }
    //    Add a platform to allow user to inout a message
    @GetMapping("/addmessages")
    public String messageForm(Model model){
        model.addAttribute("message", messageRepository.findAll());
        model.addAttribute("message", new Message());

        return "messageform";
    }
    //    Take the input and store it
    @PostMapping("/processmessage")
    public String processForm(@Valid Message message,
                              BindingResult result){
        if(result.hasErrors()){
            return "messageform";
        }
        messageRepository.save(message);
        return "redirect:/";
    }
    //    Creates a details model for user to lookup details of their posts
    @RequestMapping("/detail/message/{id}")
    public String showMessage(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messagelist";
    }
    //    User can make editions of their posts
    @RequestMapping("/update/message{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }
    @RequestMapping("/delete/message{id}")
    public String delMessage(@PathVariable("id")long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }


    @RequestMapping("/secure")
    public String secure(Model model) {
        // Gets the currently logged in user and maps it to "user" in the Thymeleaf template
        model.addAttribute("user", userService.getCurrentUser());
        return "secure";
    }

}
