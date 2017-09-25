package org.valuereporter.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Controller
public class WhdyahGuiController {
    private static final Logger log = LoggerFactory.getLogger(WhdyahGuiController.class);

    public static final String SERVICE_NAME = "serviceName";
    public static final String METHOD_NAME = "methodName";
    public static final String FROM = "from";
    public static final String TO = "to";


    @RequestMapping("/whydah/usersession")
    public ModelAndView showSlaGraphInterval(@RequestParam(value = SERVICE_NAME, required = false) String serviceName, @RequestParam(value = METHOD_NAME, required = false) String methodName,
                                             @RequestParam(value = FROM, required = false) Long from, @RequestParam(value = TO, required = false) String to) {
        Map model = new HashMap<String,String>();
        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = "all";
        }
        model.put(SERVICE_NAME, serviceName);
        model.put(METHOD_NAME, methodName);
        model.put("username", "All");
        if (from != null) {
            model.put(FROM, from);
        } else {
            model.put(FROM, getDefaultFromTime());
        }
        if (to != null) {
            model.put(TO, to);
        }
        log.trace("Input serviceName {}, methodName {}", serviceName,methodName);
        return new ModelAndView("whydah/usersessions", "model", model);
    }

    private long getDefaultFromTime(){

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -14);   // Lets default to the last two weeks
        Date todate1 = cal.getTime();
        return todate1.getTime();
    }
}
