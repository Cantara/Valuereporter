package org.valuereporter.gui;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 29.07.15.
 */
@Controller
public class ImplementedController {
    private static final Logger log = getLogger(ImplementedController.class);
    public static final String SERVICE_NAME = "serviceName";
    public static final String METHOD_NAME = "methodName";
    public static final String FROM = "from";
    public static final String TO = "to";

    @RequestMapping("/implemented")
    public ModelAndView showSlaGraph(@RequestParam(value = SERVICE_NAME, required = true) String serviceName, @RequestParam(value = METHOD_NAME, required = true) String methodName) {
        Map model = new HashMap<String,String>();
        model.put(serviceName, serviceName);
        model.put(METHOD_NAME, methodName);
        log.trace("Input SERVICE_NAME {}, methodName {}", serviceName,methodName);
        return new ModelAndView("implemented", "model", model);
    }
}
