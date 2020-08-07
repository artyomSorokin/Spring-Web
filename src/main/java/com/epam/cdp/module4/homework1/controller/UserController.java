package com.epam.cdp.module4.homework1.controller;

import com.epam.cdp.module4.homework1.entity.UserEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

import static com.epam.cdp.module4.homework1.util.WebConstant.INFO_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.MESSAGE_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.USERS_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.USERS_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.USER_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.USER_TEMPLATE_NAME;

@Controller
@Validated
public class UserController {

    @Resource
    private BookingFacade bookingFacade;

    /**
     * Finds user by id.
     *
     * @param userId id of User.
     * @param model  model on view.
     * @return user template.
     */
    @GetMapping("/user/id/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String getUserById(
            @PathVariable(name = "userId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "User id must be greater than 0")
                    String userId, Model model) {
        User user = bookingFacade.getUserById(Long.valueOf(userId));
        model.addAttribute(USER_ATTRIBUTE_NAME, user);
        return USER_TEMPLATE_NAME;
    }

    /**
     * Finds user by email.
     *
     * @param email User email.
     * @param model model on view.
     * @return user template.
     */
    @GetMapping("/user/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public String getUserByEmail(
            @PathVariable(name = "email")
            @NotEmpty(message = "Email should not be empty")
                    String email, Model model) {
        User user = bookingFacade.getUserByEmail(email);
        model.addAttribute(USER_ATTRIBUTE_NAME, user);
        return USER_TEMPLATE_NAME;
    }

    /**
     * Finds user by name.
     *
     * @param name     User name.
     * @param pageSize page size.
     * @param pageNum  page num.
     * @param model    model on view.
     * @return user template.
     */
    @GetMapping("/users/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public String getUsersByName(
            @PathVariable(name = "name")
            @NotEmpty(message = "Name should not be empty")
                    String name,
            @RequestParam(name = "pageSize")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page size must be greater than 0")
                    String pageSize,
            @RequestParam(name = "pageNum")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page num must be greater than 0")
                    String pageNum,
            Model model) {
        List<User> users = bookingFacade.getUsersByName(name, Integer.valueOf(pageSize), Integer.valueOf(pageNum));
        model.addAttribute(USERS_ATTRIBUTE_NAME, users);
        return USERS_TEMPLATE_NAME;
    }

    /**
     * Creates a user entity.
     *
     * @param userParameters parameters for create User.
     * @param model          model on view.
     * @return user template.
     */
    @PostMapping("/user")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String createUser(
            @RequestBody
                    Map<String, String> userParameters, Model model) {
        User userToCreate = wrapToUser(userParameters);
        User createdUser = bookingFacade.createUser(userToCreate);
        model.addAttribute(USER_ATTRIBUTE_NAME, createdUser);
        return USER_TEMPLATE_NAME;
    }

    /**
     * Updates the user entity.
     *
     * @param userParameters for update User.
     * @param model          model on view.
     * @return user template.
     */
    @PutMapping("/user")
    public String updateUser(
            @RequestBody
                    Map<String, String> userParameters, Model model) {
        User userToUpdate = wrapToUser(userParameters);
        User updatedUser = bookingFacade.updateUser(userToUpdate);
        if (updatedUser == null) {
            model.addAttribute(
                    MESSAGE_ATTRIBUTE_NAME,
                    "Could not update event, please check id" + userToUpdate.getId() + "'");
            return INFO_TEMPLATE_NAME;
        }
        model.addAttribute(USER_ATTRIBUTE_NAME, updatedUser);
        return USER_TEMPLATE_NAME;
    }

    /**
     * Deletes the user entity.
     *
     * @param userId User id.
     * @param model  model on view.
     * @return info template with result of deleting.
     */
    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteUser(
            @PathVariable(name = "userId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "User id must be positive integer")
                    String userId, Model model) {
        if (bookingFacade.deleteUser(Long.valueOf(userId))) {
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "User with id '" + userId + "' has been deleted");
        } else {
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Could not delete, please check id '" + userId + "'");
        }
        return INFO_TEMPLATE_NAME;
    }

    private User wrapToUser(@RequestBody Map<String, String> userParameters) {
        String id = userParameters.get("id");
        String name = userParameters.get("name");
        String email = userParameters.get("email");
        User user = new UserEntity();
        if (!StringUtils.isEmpty(id)) {
            user.setId(Long.valueOf(id));
        }
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
