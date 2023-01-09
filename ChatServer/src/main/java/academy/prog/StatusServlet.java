package academy.prog;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/status")
public class StatusServlet extends HttpServlet {
    private final Users users = Users.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String login = req.getParameter("login");
        Status status;
        try {
            status = Status.valueOf(req.getParameter("status"));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (users.setStatus(login, status)) {
            try (PrintWriter pw = resp.getWriter()) {
                pw.write("Status changed to " + status);
            } catch (IOException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

    }
}
