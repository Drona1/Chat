package academy.prog;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/reg")
public class RegistrationServlet extends HttpServlet {
    private final Users users = Users.getInstance();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String login = req.getParameter("login");
        String pass = req.getParameter("pass");

        try(PrintWriter pw = resp.getWriter()){
            if (users.registerUser(login,pass)) {
                pw.print("Success");
            }else{
                pw.print("User with this login exists. Choose another.");
            }
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
