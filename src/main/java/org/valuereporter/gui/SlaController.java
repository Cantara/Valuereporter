package org.valuereporter.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.valuereporter.utils.XSSFilter.hasXssRisk;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Controller
public class SlaController {
    private static final Logger log = LoggerFactory.getLogger(SlaController.class);

    public static final String SERVICE_NAME = "serviceName";
    public static final String METHOD_NAME = "methodName";
    public static final String FROM = "from";
    public static final String TO = "to";

    @RequestMapping("/sla")
    public ModelAndView showSlaGraph(@RequestParam(value = SERVICE_NAME, required = true) String serviceName, @RequestParam(value = METHOD_NAME, required = true) String methodName) {
        if (hasXssRisk(serviceName) || hasXssRisk(methodName)) {
            throw new IllegalInputException();
        }
        Map model = new HashMap<String,String>();
        model.put(serviceName, serviceName);
        model.put(METHOD_NAME, methodName);
        log.trace("Input SERVICE_NAME {}, methodName {}", serviceName,methodName);
        return new ModelAndView("sla", "model", model);
    }
    @RequestMapping("/slahc")
    public ModelAndView showSlaGraphHighChart(@RequestParam(value = SERVICE_NAME, required = true) String serviceName, @RequestParam(value = METHOD_NAME, required = true) String methodName) {
        if (hasXssRisk(serviceName) || hasXssRisk(methodName)) {
            throw new IllegalInputException();
        }
        Map model = new HashMap<String,String>();
        model.put(serviceName, serviceName);
        model.put(METHOD_NAME, methodName);
        log.trace("Input SERVICE_NAME {}, methodName {}", serviceName,methodName);
        return new ModelAndView("slahc", "model", model);
    }

    @RequestMapping("/slainterval")
    public ModelAndView showSlaGraphInterval(@RequestParam(value = SERVICE_NAME, required = true) String serviceName, @RequestParam(value = METHOD_NAME, required = true) String methodName,
                                             @RequestParam(value = FROM, required = false) Long from, @RequestParam(value = TO, required = false) String to) {
        if (hasXssRisk(serviceName) || hasXssRisk(methodName)) {
            throw new IllegalInputException();
        }
        Map model = new HashMap<String,String>();
        model.put(SERVICE_NAME, serviceName);
        model.put(METHOD_NAME, methodName);
        if (from != null) {
            model.put(FROM, from);
        }
        if (to != null) {
            model.put(TO, to);
        }
        log.trace("Input SERVICE_NAME {}, methodName {}", serviceName,methodName);
        return new ModelAndView("slainterval", "model", model);
    }
}
