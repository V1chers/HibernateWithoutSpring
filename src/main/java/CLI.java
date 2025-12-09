import java.time.Instant;
import java.util.Scanner;

public class CLI {
    final static Scanner scanner = new Scanner(System.in);
    static Service service;

    public static void main(String[] args) {
        commandLineInterface();
    }

    public static void commandLineInterface() {
        Scanner scanner = new Scanner(System.in);
        boolean keepGoing = true;

        System.out.println("Выберите способ работы с базой данных");
        System.out.println("1. Через Session");
        System.out.println("2. Через Entity Manager");
        UserDaoType userDaoType = UserDaoType.of(Integer.parseInt(scanner.nextLine()));
        service = new Service(userDaoType);

        while (keepGoing) {
            System.out.println("Выберите действие:");
            System.out.println("1. Добавить пользователя");
            System.out.println("2. Найти пользователя по id");
            System.out.println("3. Обновить данные пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("5. Выйти из приложения");

            switch (scanner.nextLine()) {
                case "1" -> addUser();
                case "2" -> findUser();
                case "3" -> updateUser();
                case "4" -> removeUser();
                case "5" -> {
                    scanner.close();
                    keepGoing = false;

                    if (userDaoType.equals(UserDaoType.ENTITY_MANAGER)) {
                        HibernateFactory.getEntityManager().close();
                    }
                }
                default -> System.out.println("Была введена неправильная команда");
            }

        }
    }

    public static void addUser() {
        Users users = new Users();

        System.out.println("Введите имя:");
        users.setName(scanner.nextLine());

        System.out.println("Введите почту:");
        users.setEmail(scanner.nextLine());

        System.out.println("Введите возраст:");
        try {
            users.setAge(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Было введено число неправильного формата");
            return;
        }

        users.setCreatedAt(Instant.now());

        System.out.println(service.addUser(users));
    }

    public static void findUser() {
        System.out.println("Введите id пользователя:");
        try {
            System.out.println(service.findUser(Integer.parseInt(scanner.nextLine())));
        } catch (NumberFormatException e) {
            System.out.println("Было введено число неправильного формата");
        }
    }

    public static void updateUser() {
        Users users = new Users();

        System.out.println("Введите id пользователя:");
        try {
            users.setId(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Было введено число неправильного формата");
        }

        System.out.println("Введите имя:");
        users.setName(scanner.nextLine());

        System.out.println("Введите почту:");
        users.setEmail(scanner.nextLine());

        System.out.println("Введите возраст:");
        try {
            users.setAge(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Было введено число неправильного формата");
            return;
        }

        System.out.println(service.updateUser(users));
    }

    public static void removeUser() {
        System.out.println("Введите id пользователя:");
        try {
            service.removeUser(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Было введено число неправильного формата");
        }
    }
}
