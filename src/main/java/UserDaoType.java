import java.util.stream.Stream;

public enum UserDaoType {
    SESSION(1),
    ENTITY_MANAGER(2);

    private final int id;

    UserDaoType(int id) {
        this.id = id;
    }

    public static UserDaoType of(int id) {
        return Stream.of(UserDaoType.values())
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static UserDao getUserDao(UserDaoType userDaoType) {
        switch (userDaoType) {
            case SESSION -> {
                return new UserSessionDao();
            }
            case ENTITY_MANAGER -> {
                return new UserEntityManagerDao();
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public int getId() {
        return id;
    }
}
