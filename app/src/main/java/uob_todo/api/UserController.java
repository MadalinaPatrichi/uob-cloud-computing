package uob_todo.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uob_todo.api.exceptions.UnauthorizedException;

import java.security.Principal;
import java.util.Arrays;

@RestController
public class UserController {

    @Autowired
    private Environment environment;

    private boolean isProfileActive(String name) {
        return Arrays.asList(environment.getActiveProfiles()).contains(name);
    }

    @RequestMapping(path = "/api/user")
    public UserItem user(Principal principal) throws UnauthorizedException{
        if (principal == null) {
            if (isProfileActive("secured")) {
                throw new UnauthorizedException("you are not logged in");
            } else {
                return new UserItem("Anonymous", "");
            }
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oAuth2Authentication.getDetails();
        return new UserItem(
                principal.getName(),
                details.getTokenValue()
        );
    }

}
