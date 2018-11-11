package course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

import course.entity.User;
import course.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private UserRepository repository;

    @RequestMapping(value = "saveUser", method = RequestMethod.POST)
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam(value = "id", required = false) Long userId) {
        if (!bindingResult.hasErrors()) { // validation errors
            if (user.getPassword().equals(user.getPasswordCheck())) { // check password match		
                if (repository.findByUsername(user.getUsername()) == null) { // validate username
                    String pwd = user.getPassword();
                    BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
                    String hashPwd = bc.encode(pwd);
                    user.setPassword(hashPwd);
                    repository.save(user);
                    return "users";
                } else {
                    bindingResult.rejectValue("username", "error.userexists", "Username already exists");
                }
            } else {
                bindingResult.rejectValue("passwordCheck", "error.pwdmatch", "Passwords does not match");
            }
        }
        return "formUser";
    }

    @RequestMapping("users")
    public String index(Model model) {
        List<User> users = (List<User>) repository.findAllByOrderByUsernameAsc();
        model.addAttribute("users", users);
        return "users";
    }

    @RequestMapping(value = "addUser")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("title", "Add User");
        return "formUser";
    }

    @RequestMapping(value = "editUser/{id}")
    public String editUser(@PathVariable("id") Long userId, Model model) {
        model.addAttribute("user", repository.findById(userId));
        model.addAttribute("title", "Edit User");
        return "formUser";
    }

    @RequestMapping(value = "deleteUser/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") Long userId, Model model) {
        repository.deleteById(userId);
        return "redirect:/users";
    }
}
