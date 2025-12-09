public class Service {
    private final UserDao userDao;

    public Service(UserDaoType userDaoType) {
        userDao = UserDaoType.getUserDao(userDaoType);
    }

    public Users addUser(Users users) {
        return userDao.save(users);
    }

    public Users findUser(int id) {
        return userDao.findById(id);
    }

    public Users updateUser(Users users) {
        Users updatingUsers = userDao.findById(users.getId());

        if (updatingUsers == null) {
            System.out.println("Пользователь с таким id не существует");
            return null;
        }

        if (!users.getName().isBlank()) {
            updatingUsers.setName(users.getName());
        }
        if (!users.getEmail().isBlank()) {
            updatingUsers.setEmail(users.getEmail());
        }
        if (users.getAge() != null) {
            updatingUsers.setAge(users.getAge());
        }

        return userDao.update(updatingUsers);
    }

    public void removeUser(int id) {
        Users users = userDao.findById(id);
        userDao.delete(users);
    }
}
