package org.marble.commons.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserRestController {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public Principal user(Principal user) {
        return user;
    }
}
