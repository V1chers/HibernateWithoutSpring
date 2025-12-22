import jakarta.persistence.EntityManager;

public class UserEntityManagerDao implements UserDao {
    public Users findById(int id) {
        EntityManager em = HibernateFactory.getEntityManager();
        Users users = em.find(Users.class, id);
        if (users != null) {
            em.detach(users);
        }

        return users;
    }

    public Users save(Users users) {
        EntityManager em = HibernateFactory.getEntityManager();
        em.getTransaction().begin();
        em.persist(users);
        em.getTransaction().commit();
        em.detach(users);

        return users;
    }

    public Users update(Users users) {
        EntityManager em = HibernateFactory.getEntityManager();
        em.getTransaction().begin();
        Users managedUsers = em.merge(users);
        em.getTransaction().commit();
        em.detach(managedUsers);

        return managedUsers;
    }

    public void delete(Users users) {
        EntityManager em = HibernateFactory.getEntityManager();
        em.getTransaction().begin();
        Users managedUser = em.find(Users.class, users.getId());

        if (managedUser == null) {
            return;
        }

        em.remove(managedUser);
        em.getTransaction().commit();
    }
}
