package edu.acc.j2ee.hubbub.domain.profile;

import edu.acc.j2ee.hubbub.DaoException;
import edu.acc.j2ee.hubbub.domain.user.User;
import edu.acc.j2ee.hubbub.domain.user.UserDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ProfileDaoDbImpl extends ProfileDaoImpl {
    private final Connection conn;
    private final UserDao    userDao;
    
    public ProfileDaoDbImpl(Connection conn, UserDao userDao) {
        this.conn = conn;
        this.userDao = userDao;
    }

    @Override
    public void update(Profile owned, Profile bean) {
        String sql = "UPDATE profiles SET firstname = ?, lastname = ?, email " +
                    "= ?,  biography = ? WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, bean.getFirstName());
            pst.setString(2, bean.getLastName());
            pst.setString(3, bean.getEmailAddress());
            pst.setString(4, bean.getBiography());
            pst.setInt(5, owned.getId());
            pst.executeUpdate();
            super.update(owned, bean);
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
    @Override
    public void addProfile(Profile profile) {
        String sql = "INSERT INTO profiles (owner) VALUES (?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, profile.getOwner().getUsername());
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                profile.setId(rs.getInt(1));
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }        
    }

    @Override
    public Profile findByUser(User user) {
        return findByUsername(user.getUsername());
    }

    @Override
    public Profile findByUsername(String username) {
        String sql = "SELECT * FROM profiles WHERE owner = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                User owner = userDao.findByUsername(rs.getString("owner"));
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String email = rs.getString("email");
                String biography = rs.getString("biography");
                int id = rs.getInt("id");
                Profile profile = new Profile(owner, firstName, lastName, email, biography, id);
                return profile;
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
}
