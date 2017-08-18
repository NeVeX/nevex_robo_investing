package com.nevex.roboinvesting.ws.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Mark Cunningham on 8/18/2017.
 */
@Controller
@RequestMapping
public class HomeViewController {

    @RequestMapping(method = RequestMethod.GET)
    public String getHomepage() {
        return "homepage.html";
    }
}
