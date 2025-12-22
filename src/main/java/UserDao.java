public interface UserDao {

    Users findById(int id);

    Users save(Users users);

    Users update(Users users);

    void delete(Users users);
}
