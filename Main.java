import java.util.*;
public class Main {
    public static void main(String[] args) {
        Library library = new Library("Городская Library");

        // top 5 бестселлеров of all time
        library.addBook(new FictionBook("Дон Кихот", "Мигель де Сервантес"));
        library.addBook(new FictionBook("Над пропастью во ржи", "Дж.Д. Сэлинджер"));
        library.addBook(new FictionBook("Властелин колец", "Дж.Р.Р. Толкин"));
        library.addBook(new FictionBook("Маленький принц", "Антуан де Сент-Экзюпери"));
        library.addBook(new FictionBook("Гарри Поттер и Философский камень", "Дж.К. Роулинг"));

        //пользователи and админ
        library.addMember(new Member("Айбек"));
        library.addMember(new Member("Жанар"));
        library.addMember(new Member("Алтынай"));
        library.addMember(new Member("Ербол"));
        library.addMember(new Member("Данель"));

        Admin admin = new Admin("Даулет", "самыйлучший777");
        library.setAdmin(admin);

        LibraryApp app = new LibraryApp(library);
        app.run();
    }
}

class Library {
    private String name;
    private List<Book> books;
    private List<Member> members;
    private Admin admin;

    public Library(String name) {
        this.name = name;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
    }
}

abstract class Book {
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public abstract void displayDetails();
}

class FictionBook extends Book {
    public FictionBook(String title, String author) {
        super(title, author);
    }

    @Override
    public void displayDetails() {
        System.out.println("Fiction: " + getTitle() + " by " + getAuthor());
    }
}

class NonFictionBook extends Book {
    public NonFictionBook(String title, String author) {
        super(title, author);
    }

    @Override
    public void displayDetails() {
        System.out.println("Non-Fiction: " + getTitle() + " by " + getAuthor());
    }
}

class Member {
    private String name;
    private List<Book> borrowedBooks;

    public Member(String name) {
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(Book book) {
        if (book.isAvailable()) {
            borrowedBooks.add(book);
            book.setAvailable(false);
            System.out.println(name + " borrowed " + book.getTitle());
        } else {
            System.out.println(book.getTitle() + " is not available.");
        }
    }

    public void returnBook(Book book) {
        if (borrowedBooks.remove(book)) {
            book.setAvailable(true);
            System.out.println(name + " returned " + book.getTitle());
        } else {
            System.out.println(name + " has not borrowed " + book.getTitle());
        }
    }
}

class Admin {
    private String username;
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
}

class LibraryApp {
    private static LibraryApp instance;
    private final Library library;
    private final Scanner scanner;

    public LibraryApp(Library library) {
        this.library = library;
        this.scanner = new Scanner(System.in);
    }

    public static LibraryApp getInstance(Library library) {
        if (instance == null) {
            instance = new LibraryApp(library);
        }
        return instance;
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Добро Пожаловать в " + library.getName());
            System.out.println("1. Добавить книгу\n2. Добавить пользователя\n3. Взять книгу\n4. Вернуть книгу\n5. Список книг\n6. Логин Админа\n7. Выход");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> borrowBook();
                case 4 -> returnBook();
                case 5 -> listBooks();
                case 6 -> adminLogin();
                case 7 -> exit = true;
                default -> System.out.println("Неправильный ввод, пожалуйста повторите");
            }
        }
    }

    private void addBook() {
        System.out.println("Введите название книги:");
        String title = scanner.nextLine();
        System.out.println("Введите имя автора:");
        String author = scanner.nextLine();
        System.out.println("Книга является вымослом (да/нет)?");
        String type = scanner.nextLine();

        Book book = type.equalsIgnoreCase("yes")
                ? new FictionBook(title, author)
                : new NonFictionBook(title, author);
        library.addBook(book);
        System.out.println("Книга добавлена: " + title);
    }

    private void addMember() {
        System.out.println("Введите имя пользователя:");
        String name = scanner.nextLine();
        Member member = new Member(name);
        library.addMember(member);
        System.out.println("Пользователь добавлен: " + name);
    }

    private void borrowBook() {
        System.out.println("Введите имя пользователя:");
        String memberName = scanner.nextLine();
        System.out.println("Введите название книги:");
        String bookTitle = scanner.nextLine();

        Optional<Member> member = library.getMembers().stream()
                .filter(m -> m.getName().equalsIgnoreCase(memberName))
                .findFirst();

        Optional<Book> book = library.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(bookTitle) && b.isAvailable())
                .findFirst();

        if (member.isPresent() && book.isPresent()) {
            member.get().borrowBook(book.get());
        } else {
            System.out.println("Пользователь или книга не были найдены или книга недоступна");
        }
    }

    private void returnBook() {
        System.out.println("Введите имя пользователя:");
        String memberName = scanner.nextLine();
        System.out.println("Введите название книги:");
        String bookTitle = scanner.nextLine();

        Optional<Member> member = library.getMembers().stream()
                .filter(m -> m.getName().equalsIgnoreCase(memberName))
                .findFirst();

        Optional<Book> book = library.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(bookTitle))
                .findFirst();

        if (member.isPresent() && book.isPresent()) {
            member.get().returnBook(book.get());
        } else {
            System.out.println("Пользователь или Книга не были найдены");
        }
    }

    private void listBooks() {
        library.getBooks().forEach(Book::displayDetails);
    }

    private void adminLogin() {
        System.out.println("Введите имя Админа:");
        String username = scanner.nextLine();
        System.out.println("Введите пароль Админа:");
        String password = scanner.nextLine();

        if (library.getAdmin() != null && library.getAdmin().getUsername().equals(username) && library.getAdmin().authenticate(password)) {
            System.out.println("Успешный вход!");
        } else {
            System.out.println("Неверные данные админа");
        }
    }
}