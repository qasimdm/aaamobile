package app15.aaamobile.model;

/**
 * Created by umyhafzaqa on 2016-11-07.
 */
public class User {

    private String uid;
    private String email;
    private String name;
    private String password;

    public User(){
    }

    public User(String uid, String email, String name, String password){
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.password = password;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}