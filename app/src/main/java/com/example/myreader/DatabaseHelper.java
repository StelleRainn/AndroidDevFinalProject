package com.example.myreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reading_platform.db";
    private static final int DATABASE_VERSION = 2;
    private static DatabaseHelper instance;
    private Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE book (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, author TEXT, upload_user TEXT NOT NULL, upload_time TEXT NOT NULL, file_path TEXT NOT NULL, price REAL DEFAULT 0, is_free INTEGER NOT NULL DEFAULT 0, cover_path TEXT, FOREIGN KEY(upload_user) REFERENCES user(username))");
        db.execSQL("CREATE TABLE order_book (id INTEGER PRIMARY KEY AUTOINCREMENT, book_id INTEGER NOT NULL, user_id INTEGER NOT NULL, buy_time TEXT NOT NULL, FOREIGN KEY(book_id) REFERENCES book(id), FOREIGN KEY(user_id) REFERENCES user(id))");
        db.execSQL("CREATE TABLE comment (id INTEGER PRIMARY KEY AUTOINCREMENT, book_id INTEGER NOT NULL, user_id INTEGER NOT NULL, content TEXT NOT NULL, comment_time TEXT NOT NULL, FOREIGN KEY(book_id) REFERENCES book(id), FOREIGN KEY(user_id) REFERENCES user(id))");
        db.execSQL("INSERT INTO user (username, password) VALUES ('admin', '123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE book ADD COLUMN cover_path TEXT");
        }
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("INSERT INTO user (username, password) VALUES (?, ?)", new Object[]{username, password});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username = ? AND password = ?", new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    @SuppressLint("Range")
    public List<Book> getAllBooks() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book", null);
        List<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setId(cursor.getInt(cursor.getColumnIndex("id")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setUploadUser(cursor.getString(cursor.getColumnIndex("upload_user")));
            book.setUploadTime(cursor.getString(cursor.getColumnIndex("upload_time")));
            book.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
            book.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            book.setFree(cursor.getInt(cursor.getColumnIndex("is_free")) == 1);
            book.setCoverPath(cursor.getString(cursor.getColumnIndex("cover_path")));
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public String loadFileContent(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            android.util.Log.e("DatabaseHelper", "文件未找到: " + filePath);
            return null;
        }
        return filePath;
    }

    public String getFreePagesContent(String filePath, int freePages) {
        return loadFileContent(filePath);
    }

    @SuppressLint("Range")
    public List<Book> getMyPublishedBooks(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book WHERE upload_user = ?", new String[]{username});
        List<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setId(cursor.getInt(cursor.getColumnIndex("id")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setUploadUser(cursor.getString(cursor.getColumnIndex("upload_user")));
            book.setUploadTime(cursor.getString(cursor.getColumnIndex("upload_time")));
            book.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
            book.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            book.setFree(cursor.getInt(cursor.getColumnIndex("is_free")) == 1);
            book.setCoverPath(cursor.getString(cursor.getColumnIndex("cover_path")));
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("DELETE FROM book WHERE id = ?", new Object[]{bookId});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("Range")
    public Book getBookById(int bookId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book WHERE id = ?", new String[]{String.valueOf(bookId)});
        Book book = null;
        if (cursor.moveToFirst()) {
            book = new Book();
            book.setId(cursor.getInt(cursor.getColumnIndex("id")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setUploadUser(cursor.getString(cursor.getColumnIndex("upload_user")));
            book.setUploadTime(cursor.getString(cursor.getColumnIndex("upload_time")));
            book.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
            book.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            book.setFree(cursor.getInt(cursor.getColumnIndex("is_free")) == 1);
            book.setCoverPath(cursor.getString(cursor.getColumnIndex("cover_path")));
        }
        cursor.close();
        return book;
    }

    public boolean updateBook(int bookId, String name, String author, double price, String coverPath) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("UPDATE book SET name = ?, author = ?, price = ?, cover_path = ? WHERE id = ?",
                    new Object[]{name, author, price, coverPath, bookId});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("Range")
    public List<Book> searchBooks(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book WHERE name LIKE ? OR author LIKE ?", new String[]{"%" + keyword + "%", "%" + keyword + "%"});
        List<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setId(cursor.getInt(cursor.getColumnIndex("id")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setUploadUser(cursor.getString(cursor.getColumnIndex("upload_user")));
            book.setUploadTime(cursor.getString(cursor.getColumnIndex("upload_time")));
            book.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
            book.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            book.setFree(cursor.getInt(cursor.getColumnIndex("is_free")) == 1);
            book.setCoverPath(cursor.getString(cursor.getColumnIndex("cover_path")));
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public boolean uploadBook(String username, String name, String author, double price, String filePath, String coverPath) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("INSERT INTO book (name, author, upload_user, upload_time, file_path, price, is_free, cover_path) VALUES (?, ?, ?, datetime('now'), ?, ?, ?, ?)",
                    new Object[]{name, author, username, filePath, price, price == 0 ? 1 : 0, coverPath});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean buyBook(String username, int bookId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM order_book WHERE user_id = (SELECT id FROM user WHERE username = ?) AND book_id = ?", new String[]{username, String.valueOf(bookId)});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            int userId = getUserIdByUsername(username);
            db.execSQL("INSERT INTO order_book (book_id, user_id, buy_time) VALUES (?, ?, datetime('now'))",
                    new Object[]{bookId, userId});
            return true;
        }
    }

    public boolean hasBoughtBook(String username, int bookId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM order_book WHERE user_id = (SELECT id FROM user WHERE username = ?) AND book_id = ?", new String[]{username, String.valueOf(bookId)});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public String getBookContent(int bookId, String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_free, file_path FROM book WHERE id = ?", new String[]{String.valueOf(bookId)});
        String filePath = null;
        boolean isFree = false;
        if (cursor.moveToNext()) {
            isFree = cursor.getInt(0) == 1;
            filePath = cursor.getString(1);
        }
        cursor.close();

        if (isFree || hasBoughtBook(username, bookId)) {
            return loadFileContent(filePath);
        } else {
            return getFreePagesContent(filePath, 2);
        }
    }

    public List<Comment> getComments(int bookId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT c.id, u.username, c.content, c.comment_time FROM comment c JOIN user u ON c.user_id = u.id WHERE c.book_id = ? ORDER BY c.comment_time DESC", new String[]{String.valueOf(bookId)});
        List<Comment> comments = new ArrayList<>();
        while (cursor.moveToNext()) {
            Comment comment = new Comment();
            comment.setId(cursor.getInt(0));
            comment.setUsername(cursor.getString(1));
            comment.setContent(cursor.getString(2));
            comment.setCommentTime(cursor.getString(3));
            comments.add(comment);
        }
        cursor.close();
        return comments;
    }

    public boolean postComment(String username, int bookId, String content) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT is_free FROM book WHERE id = ?", new String[]{String.valueOf(bookId)});
        if (cursor.moveToNext() && cursor.getInt(0) == 0) {
            if (!hasBoughtBook(username, bookId)) {
                cursor.close();
                return false;
            }
        }
        cursor.close();
        int userId = getUserIdByUsername(username);
        db.execSQL("INSERT INTO comment (book_id, user_id, content, comment_time) VALUES (?, ?, ?, datetime('now'))",
                new Object[]{bookId, userId, content});
        return true;
    }

    public List<Order> getOrders(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT o.id, b.name, b.price, o.buy_time FROM order_book o JOIN book b ON o.book_id = b.id WHERE o.user_id = (SELECT id FROM user WHERE username = ?) ORDER BY o.buy_time DESC", new String[]{username});
        List<Order> orders = new ArrayList<>();
        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setId(cursor.getInt(0));
            order.setBookName(cursor.getString(1));
            order.setPrice(cursor.getDouble(2));
            order.setBuyTime(cursor.getString(3));
            orders.add(order);
        }
        cursor.close();
        return orders;
    }

    @SuppressLint("Range")
    public List<SalesRecord> getSalesRecords(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT o.id, b.name, b.price, u.username, o.buy_time " +
                        "FROM order_book o " +
                        "JOIN book b ON o.book_id = b.id " +
                        "JOIN user u ON o.user_id = u.id " +
                        "WHERE b.upload_user = ? " +
                        "ORDER BY o.buy_time DESC",
                new String[]{username});
        List<SalesRecord> salesRecords = new ArrayList<>();
        while (cursor.moveToNext()) {
            SalesRecord record = new SalesRecord();
            record.setId(cursor.getInt(cursor.getColumnIndex("id")));
            record.setBookName(cursor.getString(cursor.getColumnIndex("name")));
            record.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            record.setBuyerUsername(cursor.getString(cursor.getColumnIndex("username")));
            record.setBuyTime(cursor.getString(cursor.getColumnIndex("buy_time")));
            salesRecords.add(record);
        }
        cursor.close();
        return salesRecords;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM user WHERE username = ?", new String[]{username});
        int userId = -1;
        if (cursor.moveToNext()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    public boolean isUserHasUploadedBook(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM book WHERE upload_user = ?", new String[]{username});
        boolean result = false;
        if (cursor.moveToNext() && cursor.getInt(0) > 0) {
            result = true;
        }
        cursor.close();
        return result;
    }

    public String getBookFilePath(int bookId, String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT file_path FROM book WHERE id = ? AND (is_free = 1 OR EXISTS (SELECT 1 FROM order_book WHERE book_id = ? AND user_id = (SELECT id FROM user WHERE username = ?)))",
                new String[]{String.valueOf(bookId), String.valueOf(bookId), username});
        String filePath = null;
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(0);
        }
        cursor.close();
        return filePath;
    }

    @SuppressLint("Range")
    public int getBookIdByName(String bookName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM book WHERE name = ?", new String[]{bookName});
        int bookId = -1;
        if (cursor.moveToNext()) {
            bookId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return bookId;
    }

    @SuppressLint("Range")
    public String getSellerByUsername(String bookName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT upload_user FROM book WHERE name = ?", new String[]{bookName});
        String sellerUsername = null;
        if (cursor.moveToFirst()) {
            sellerUsername = cursor.getString(cursor.getColumnIndex("upload_user"));
        }
        cursor.close();
        return sellerUsername;
    }
}