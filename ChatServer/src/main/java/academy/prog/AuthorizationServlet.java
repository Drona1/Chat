package academy.prog;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
@WebServlet("/login")
public class AuthorizationServlet extends HttpServlet {
    private final Users users = Users.getInstance();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
        String login = req.getParameter("login");
        String pass = req.getParameter("pass");

        try(PrintWriter pw = resp.getWriter()){
            if (users.authorizeUser(login,pass)) {
                pw.print("Success");
            }else{
                pw.print("Wrong login/password. Try again.");
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
