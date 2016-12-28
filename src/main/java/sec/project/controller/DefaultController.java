package sec.project.controller;

import java.util.Arrays;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.session.StandardManager;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

        @Bean
        public TomcatEmbeddedServletContainerFactory servletContainer() {
            TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
            factory.setTomcatContextCustomizers(Arrays.asList(new CustomCustomizer()));
            return factory;
        }
        // new manager to make my own sessionID generator
        public class MySessionManager extends StandardManager {
            int id = 0;
            @Override
            protected synchronized String generateSessionId() { 
            String sessionId = String.valueOf(id);
            id++;
            return sessionId;
            }
        }
        
        class CustomCustomizer implements TomcatContextCustomizer {

        private Manager myManager = new MySessionManager();

            @Override
            public void customize(Context context) {
                
                context.setManager(myManager);
                context.getManager().getSessionIdGenerator().setSessionIdLength(1);
                context.setUseHttpOnly(false);
                context.setSessionTimeout(365*24*60*60);
                
                   
                
                
            }
        }
        
    
    @RequestMapping("*")
    public String defaultRedirectToImages() {
        return "redirect:/index";
    }
}
