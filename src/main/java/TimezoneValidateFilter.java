import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter (value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {

        String timeZone = req.getParameter("timezone").replaceAll(" ", "+");
        String[] timeZones = TimeZone.getAvailableIDs();
        for (String zoneId : timeZones) {
            if (zoneId.equals(timeZone)) {
                chain.doFilter(req, resp);
            } else {
                resp.setContentType("text/html; charset=utf-8");
                resp.setStatus(400);
                resp.getWriter().write("Invalid timezone!");
                resp.getWriter().close();
            }
        }
    }
}