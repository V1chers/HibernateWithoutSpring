import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserSessionDao implements UserDao {

    public Users findById(int id) {
        Session session = HibernateFactory.getSessionFactory().openSession();
        Users users = session.find(Users.class, id);
        if (users != null) {
            session.detach(users);
        }
        
        return users;
    }

    public Users save(Users users) {
        Session session = HibernateFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(users);
        session.detach(users);
        transaction.commit();
        session.close();

        return users;
    }

    public Users update(Users users) {
        Session session = HibernateFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Users managedUsers = session.merge(users);
        session.detach(users);
        transaction.commit();
        session.close();

        return managedUsers;
    }

    public void delete(Users users) {
        Session session = HibernateFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Users managedUser = session.find(Users.class, users.getId());

        if (managedUser == null) {
            return;
        }

        session.remove(managedUser);
        transaction.commit();
        session.close();
    }
}
