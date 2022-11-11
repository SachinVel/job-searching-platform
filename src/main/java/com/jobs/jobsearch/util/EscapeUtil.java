package com.jobs.jobsearch.util;

import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

public class EscapeUtil {

    public static String esacpeInput(String input){
        input = HtmlUtils.htmlEscape(input);
        input = JavaScriptUtils.javaScriptEscape(input);
        return input;
    }
}
