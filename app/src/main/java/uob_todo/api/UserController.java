package uob_todo.api;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uob_todo.api.exceptions.UnauthorizedException;

import java.security.Principal;

@RestController
public class UserController {

    @RequestMapping(path = "/api/user")
    public Principal user(Principal principal) throws UnauthorizedException{
        if (principal == null) {
            throw new UnauthorizedException("you are not logged in");
        }
        return principal;
    }

}
