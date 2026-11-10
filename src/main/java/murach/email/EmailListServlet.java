package murach.email;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import murach.business.User;
import murach.business.Survey;
import murach.data.UserDB;

public class EmailListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            action = "join";
        }

        String url;

        if (action.equals("join")) {
            url = "/index.html";

        } else if (action.equals("add")) {

            // Nhận thông tin người dùng
            String firstName = getText(request, "firstName");
            String lastName = getText(request, "lastName");
            String email = getText(request, "email");
            String date = getText(request, "date");

            // Kiểm tra các trường bắt buộc ở server
            if (firstName.isEmpty()
                    || lastName.isEmpty()
                    || email.isEmpty()
                    || date.isEmpty()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Please enter your first name, last name, "
                                + "email and date of birth.");
                return;
            }

            // Tạo User có cả ngày sinh
            User user = new User(firstName, lastName, email, date);

            // Nhận câu trả lời khảo sát
            String heardFrom = getText(request, "heardFrom");
            String contactVia = getText(request, "contactVia");

            boolean wantsUpdates =
                    "yes".equals(request.getParameter("wantsUpdates"));

            boolean emailAnnouncements =
                    "yes".equals(
                            request.getParameter("emailAnnouncements"));

            // Tạo đối tượng Survey riêng
            Survey survey = new Survey();
            survey.setHeardFrom(heardFrom);
            survey.setWantsUpdates(wantsUpdates);
            survey.setEmailAnnouncements(emailAnnouncements);
            survey.setContactVia(contactVia);

            // Lưu người dùng vào danh sách trong RAM
            UserDB.insert(user);

            // Chuyển hai đối tượng sang trang kết quả
            request.setAttribute("user", user);
            request.setAttribute("survey", survey);

            url = "/thanks.jsp";

        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Unknown action.");
            return;
        }

        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    private String getText(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}