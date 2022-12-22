package com.example.se302_project;

import java.io.File;
import java.sql.*;

public class DBConnection {
    private static DBConnection instance = null;
    private final String fileName;
    private Connection connection;

    private PreparedStatement insertResume, insertTemplate, insertTag, insertAttribute;

    private DBConnection() {
        this.fileName = "info.db";
        File file = new File(fileName);
        boolean firstRun = !file.exists();

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            if (firstRun) {
                Statement stat = connection.createStatement();

                stat.executeUpdate("CREATE TABLE RESUME(" +
                        "NAME TEXT PRIMARY KEY," +
                        "DATE TEXT NOT NULL," +
                        "FILE TEXT NOT NULL," +
                        "TEMPLATE_NAME TEXT NOT NULL)");

                stat.executeUpdate("CREATE TABLE TEMPLATE(" +
                        "TITLE TEXT," +
                        "ATTRIBUTES TEXT," +
                        "PRIMARY KEY (TITLE, ATTRIBUTES))");

                stat.executeUpdate("CREATE TABLE TAG(" +
                        "RESUME_NAME TEXT," +
                        "NAME TEXT," +
                        "PRIMARY KEY (NAME, RESUME_NAME))");

                stat.executeUpdate("CREATE TABLE ATTRIBUTES(" +
                        "RESUME_NAME TEXT," +
                        "KEY TEXT," +
                        "VALUE TEXT," +
                        "PRIMARY KEY (RESUME_NAME, KEY))");
            }

            insertResume = connection
                    .prepareStatement("INSERT INTO RESUME (NAME, DATE, FILE, TEMPLATE_NAME) VALUES (?,?,?,?)");
            insertTemplate = connection
                    .prepareStatement("INSERT INTO TEMPLATE (TITLE, ATTRIBUTES) VALUES (?,?)");

            insertTag = connection.prepareStatement("INSERT INTO TAG (RESUME_NAME, NAME) VALUES (?,?)");

            insertAttribute = connection
                    .prepareStatement("INSERT INTO ATTRIBUTES (RESUME_NAME, KEY, VALUE) VALUES (?,?,?)");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }
    };

    public static DBConnection getInstance() {

        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    public void addResume(Resume resume) {
        try {
            insertResume.setString(1, resume.getName());
            insertResume.setString(2, resume.getDate());
            insertResume.setString(3, resume.getfileName());
            insertResume.setString(4, resume.getTemplate().getTitle());
            insertResume.execute();

            for (int i = 0; i < resume.getTags().size(); i++) {
                insertTag.setString(1, resume.getName());
                insertTag.setString(2, resume.getTags().get(i));
                insertTag.execute();
            }

            Object[] attributeKeys = resume.getAttributes().keySet().toArray();

            for (int i = 0; i < attributeKeys.length; i++) {
                insertAttribute.setString(1, resume.getName());
                insertAttribute.setString(2, attributeKeys[i].toString());
                insertAttribute.setString(3, resume.getAttributes().get(attributeKeys[i].toString()));
                insertAttribute.execute();
            }

        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void addTemplate(Template template) {
        try {
            insertTemplate.setString(1, template.getTitle());
            insertTemplate.setString(2, "Attribute");
            insertTemplate.execute();

        } catch (SQLException e) {
            System.err.println(e);

        }
    }

    public void addTag(Resume resume, String tag) {
        try {
            insertTag.setString(1, tag);
            insertTag.setString(2, resume.getName());
            insertTag.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    /*
     * Tüm tagleri ver --> Dropdown için
     * Tüm template titleları ver --> Carousel && ListView && Dropdown
     * Tüm resume isimlerini ver --> ListView
     * BIR templatten ismi ve tüm attributeları al --> Generate Resume
     * 
     */

}
