package com.example.se302_project;

import java.io.File;
import java.io.IOException;
import java.security.DrbgParameters.Reseed;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBConnection {
    private static DBConnection instance = null;

    private final String fileName;
    private Connection connection;

    private final String indexDir;
    private final int hitsPerPage;
    private Index index;

    private PreparedStatement insertResume, insertTemplate, insertTag, insertAttribute, getTags, getTemplates,
            getResumes, getTemplateAttributes, getResumeAttributes, getResumeTags, getResumeObject,
            deleteResumeFromResume, deleteResumeFromTag, deleteResumeFromAttributes, deleteTemplate;

    DBConnection() {
        this.fileName = "info.db";
        File file = new File(fileName);
        boolean firstRun = !file.exists();

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            if (firstRun) {
                Statement stat = connection.createStatement();

                stat.executeUpdate("CREATE TABLE RESUME(" +
                        "NAME TEXT NOT NULL PRIMARY KEY," +
                        "DATE TEXT NOT NULL," +
                        "FILE TEXT NOT NULL," +
                        "TEMPLATE_NAME TEXT NOT NULL)");

                stat.executeUpdate("CREATE TABLE TEMPLATE(" +
                        "TITLE TEXT NOT NULL," +
                        "ATTRIBUTES TEXT NOT NULL," +
                        "PRIMARY KEY (TITLE, ATTRIBUTES))");

                stat.executeUpdate("CREATE TABLE TAG(" +
                        "RESUME_NAME TEXT NOT NULL," +
                        "NAME TEXT NOT NULL," +
                        "PRIMARY KEY (NAME, RESUME_NAME))");

                stat.executeUpdate("CREATE TABLE ATTRIBUTES(" +
                        "RESUME_NAME TEXT NOT NULL," +
                        "KEY TEXT NOT NULL," +
                        "VALUE TEXT NOT NULL," +
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

            getResumeAttributes = connection
                    .prepareStatement("SELECT KEY, VALUE FROM ATTRIBUTES WHERE RESUME_NAME = ?");

            getResumeTags = connection.prepareStatement("SELECT NAME FROM TAG WHERE RESUME_NAME = ?");

            deleteResumeFromResume = connection.prepareStatement("DELETE FROM RESUME WHERE NAME = ?");

            deleteResumeFromTag = connection.prepareStatement(" DELETE FROM TAG WHERE RESUME_NAME = ?");

            deleteResumeFromAttributes = connection.prepareStatement("DELETE FROM ATTRIBUTES WHERE RESUME_NAME = ?");

            getResumeObject = connection.prepareStatement("SELECT * FROM RESUME WHERE NAME = ?");

            deleteTemplate = connection.prepareStatement("DELETE FROM TEMPLATE WHERE TITLE = ?");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e);
        }

        this.indexDir = "index";
        this.hitsPerPage = 20;
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
        ArrayList<String> attributes = template.getAttributes();
        try {
            for (int i = 0; i < attributes.size(); i++) {
                insertTemplate.setString(1, template.getTitle());
                insertTemplate.setString(2, attributes.get(i));
                insertTemplate.execute();
            }

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

    public boolean templateExists(String templateName) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM TEMPLATE WHERE TITLE = ?");
            stmt.setString(1, templateName);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    public boolean resumeExists(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM resumes WHERE name = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateResume(String name, String path) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE resumes SET path = ? WHERE name = ?");
            statement.setString(1, path);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTemplateAttributes(String templateName, List<String> attributes) throws SQLException {
        deleteTemplate.setString(1, templateName);
        deleteTemplate.executeUpdate();

        for (String attribute : attributes) {
            insertTemplate.setString(1, templateName);
            insertTemplate.setString(2, attribute);
            insertTemplate.executeUpdate();
        }
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

    public HashMap<String, String> getResumeAttributes(String resumeName) throws SQLException {
        HashMap<String, String> h = new HashMap<>();
        try {
            getResumeAttributes.setString(1, resumeName);
            getResumeAttributes.execute();
            ResultSet rs = getResumeAttributes.executeQuery();

            while (rs.next()) {
                String key = rs.getString(1);
                String value = rs.getString(2);
                h.put(key, value);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return h;
    }

    public ArrayList<String> getResumeTags(String resumeName) throws SQLException {
        ArrayList<String> arr = new ArrayList<>();
        try {
            getResumeTags.setString(1, resumeName);
            getResumeTags.execute();
            ResultSet rs = getResumeTags.executeQuery();

            while (rs.next()) {
                String tag = rs.getString(1);
                arr.add(tag);
            }
        }

        catch (SQLException e) {
            System.out.println(e);
        }
        return arr;
    }

    public Template getTemplateObject(String templateName) throws SQLException {
        Template t = new Template(templateName);
        try {
            getTemplateAttributes.setString(1, templateName);
            getTemplateAttributes.execute();
            ResultSet rs = getTemplateAttributes.executeQuery();

            while (rs.next()) {
                String attribute = rs.getString(1);
                t.addAttribute(attribute);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return t;
    }

    public Resume getResumeObject(String resumeName) throws SQLException {
        Resume r = new Resume(resumeName);
        try {
            getResumeObject.setString(1, resumeName);
            getResumeObject.execute();
            ResultSet rs = getResumeObject.executeQuery();
            rs.next();

            String date = rs.getString(2);
            String file = rs.getString(3);
            String templateName = rs.getString(4);
            Template template = getTemplateObject(templateName);
            HashMap<String, String> attributes = getResumeAttributes(resumeName);
            ArrayList<String> tags = getResumeTags(resumeName);

            r.setDate(date);
            r.setfileName(file);
            r.setTemplate(template);
            r.setAttributes(attributes);
            r.setTags(tags);

        } catch (SQLException e) {
            System.out.println(e);
        }
        return r;
    }

    public void deleteResume(String resumeName) { // Find a way to merge 3 prepStmt's into one.

        try {
            deleteResumeFromResume.setString(1, resumeName);
            deleteResumeFromTag.setString(1, resumeName);
            deleteResumeFromAttributes.setString(1, resumeName);
            deleteResumeFromResume.execute();
            deleteResumeFromTag.execute();
            deleteResumeFromAttributes.execute();
            this.index.deleteDoc(resumeName);
        } catch (SQLException | IOException e) {
            System.out.println(e);
        }

    }

    public void deleteTemplate(String templateName) {
        try {
            deleteTemplate.setString(1, templateName);
            deleteTemplate.execute();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteTag(Resume resume, String tag) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM TAG WHERE RESUME_NAME = ? AND NAME = ?");
            stmt.setString(1, resume.getName());
            stmt.setString(2, tag);
            stmt.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}