package mg.exception;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class ErrorHandler {

    public static void sendError(HttpServletRequest request, HttpServletResponse response, String errorMessage, int errorCode) throws IOException {
        response.setStatus(errorCode);
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Error " + errorCode + "</title></head>");
        out.println("<body>");
        out.println("<h1>Error " + errorCode + "</h1>");
        out.println("<p>" + errorMessage + "</p>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
}
