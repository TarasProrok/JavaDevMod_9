import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.context.Context;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet (value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();
        FileTemplateResolver resolve = new FileTemplateResolver();
        resolve.setSuffix(".html");
        resolve.setPrefix("C:/Users/mac/Desktop/Java/Thymeleaf_Demo/src/templates/");
        resolve.setTemplateMode("HTML5");
        resolve.setOrder(engine.getTemplateResolvers().size());
        resolve.setCacheable(false);
        engine.addTemplateResolver(resolve);
    }

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timeZoneParam = req.getParameter("timezone").replaceAll(" ", "+");
        LocalDateTime currentTime;
        ZoneId zoneId;

        if (!timeZoneParam.isEmpty()) {

            resp.addCookie(new Cookie("timezone", timeZoneParam));
            zoneId = ZoneId.of(timeZoneParam);
            currentTime = LocalDateTime.now(zoneId);

            Cookie lastTimezone = new Cookie("lastTimeZone", timeZoneParam);
            resp.addCookie(lastTimezone);
        } else {
            currentTime = LocalDateTime.now();

            Cookie[] cookies = req.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("lastTimeZone")) {
                        timeZoneParam = cookie.getValue();
                        zoneId = ZoneId.of(timeZoneParam);
                        currentTime = LocalDateTime.now(zoneId);
                    }
                }
            }
        }

        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Context simpleContext = new Context (
                req.getLocale(),
                Map.of("formattedTime", formattedTime,
                        "timeZone", timeZoneParam)
        );

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
