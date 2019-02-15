package edu.acc.j2ee.hubbub;

import edu.acc.j2ee.hubbub.domain.post.PostDao;
import edu.acc.j2ee.hubbub.domain.post.PostDaoDbImpl;
import edu.acc.j2ee.hubbub.domain.profile.ProfileDao;
import edu.acc.j2ee.hubbub.domain.profile.ProfileDaoDbImpl;
import edu.acc.j2ee.hubbub.domain.user.UserDao;
import edu.acc.j2ee.hubbub.domain.user.UserDaoDbImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DbStartupListener implements ServletContextListener {
    private Connection conn = null;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String jdbcUrl = sce.getServletContext().getInitParameter("jdbc.url");
        String jdbcUser =sce.getServletContext().getInitParameter("jdbc.user");
        String jdbcPass =sce.getServletContext().getInitParameter("jdbc.pass");
        try {
            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
            UserDao userDao = new UserDaoDbImpl(conn);
            sce.getServletContext().setAttribute("userDao", userDao);
            PostDao postDao = new PostDaoDbImpl(conn, userDao);
            sce.getServletContext().setAttribute("postDao", postDao);
            ProfileDao profileDao = new ProfileDaoDbImpl(conn, userDao);
            sce.getServletContext().setAttribute("profileDao", profileDao);
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // get all the daos out of context scope
        try { conn.close(); }
        catch (SQLException sqle) {}
    }
}
