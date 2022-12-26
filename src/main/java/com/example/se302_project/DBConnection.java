package com.example.se302_project;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DBConnection {
    private static DBConnection instance = null;

    private final String fileName;
    private Connection connection;

    private final String indexDir;
    private final int hitsPerPage;
    private Index index;

    private PreparedStatement insertResume, insertTemplate, insertTag, insertAttribute, getTags, getTemplates,
            getResumes, getTemplateAttributes, getTemplateObject, getResumeObject;

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

            getTags = connection.prepareStatement("SELECT DISTINCT NAME FROM TAG");

            getTemplates = connection.prepareStatement("SELECT DISTINCT TITLE FROM TEMPLATE");

            getResumes = connection.prepareStatement("SELECT NAME FROM RESUME");

            getTemplateAttributes = connection.prepareStatement("SELECT ATTRIBUTES FROM TEMPLATE WHERE TITLE = ?");

            getTemplateObject = connection.prepareStatement("SELECT TITLE, ATTRIBUTE FROM TEMPLATE WHERE TITLE = ?");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }

        this.indexDir = "index";
        this.hitsPerPage = 10;
        try {
            Index index = new Index(this.indexDir, this.hitsPerPage);
            this.index = index;
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public static DBConnection getInstance() {

        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    public Index getIndex() {
        return this.index;
    }

    public void addResume(Resume resume) {
        try {
            String resume_name = resume.getName();
            String resume_date = resume.getDate();
            ArrayList<String> resume_tags = resume.getTags();

            insertResume.setString(1, resume_name);
            insertResume.setString(2, resume_date);
            insertResume.setString(3, resume.getfileName());
            insertResume.setString(4, resume.getTemplate().getTitle());
            insertResume.execute();

            for (int i = 0; i < resume_tags.size(); i++) {
                insertTag.setString(1, resume.getName());
                insertTag.setString(2, resume_tags.get(i));
                insertTag.execute();
            }

            Object[] attributeKeys = resume.getAttributes().keySet().toArray();
            String resume_text = "";
            for (int i = 0; i < attributeKeys.length; i++) {
                insertAttribute.setString(1, resume.getName());
                insertAttribute.setString(2, attributeKeys[i].toString());
                String attrVal = resume.getAttributes().get(attributeKeys[i].toString());
                insertAttribute.setString(3, attrVal);
                insertAttribute.execute();

                resume_text += attrVal + " ";
            }

            this.index.addDoc(resume_name, resume_date, resume_text, resume_tags);

        } catch (SQLException | IOException e) {
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
            insertTag.setString(1, resume.getName());
            insertTag.setString(2, tag);
            insertTag.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    public ArrayList<String> getTags() throws SQLException {
        ResultSet rs = getTags.executeQuery();
        ArrayList<String> ar = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        try {

            while (rs.next()) {
                int i = 1;
                while (i <= columnCount) {
                    ar.add(rs.getString(i++));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return ar;
    }

    public ArrayList<String> getTemplates() throws SQLException {
        ResultSet rs = getTemplates.executeQuery();
        ArrayList<String> ar = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        try {

            while (rs.next()) {
                int i = 1;
                while (i <= columnCount) {
                    ar.add(rs.getString(i++));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return ar;
    }

    public ArrayList<String> getResumes() throws SQLException {
        ResultSet rs = getResumes.executeQuery();
        ArrayList<String> ar = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        try {

            while (rs.next()) {
                int i = 1;
                while (i <= columnCount) {
                    ar.add(rs.getString(i++));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return ar;
    }

    public ArrayList<String> getTemplateAttributes(Template template) throws SQLException {

        ArrayList<String> ar = new ArrayList<>();

        try {
            getTemplateAttributes.setString(1, template.getTitle());
            getTemplateAttributes.execute();
            ResultSet rs = getTemplateAttributes.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
                int i = 1;
                while (i <= columnCount) {
                    ar.add(rs.getString(i++));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return ar;
    }

    public Template getTemplateObject(String templateName) throws SQLException {
        Template t = new Template(templateName);
        try {
            getTemplateObject.setString(1, templateName);
            getTemplateObject.execute();
            ResultSet rs = getTemplateObject.executeQuery();

            while (rs.next()) {
                String attribute = rs.getString(2);
                t.addAttribute(attribute);
                }
            
            }
        catch (SQLException e) {
                System.out.println(e);
            }
        return t;
        }
    
    }
