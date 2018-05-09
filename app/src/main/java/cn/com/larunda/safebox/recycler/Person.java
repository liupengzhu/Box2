package cn.com.larunda.safebox.recycler;

public class Person {
    private int id;
    private int userId;
    private String name;
    private boolean useDynamic;
    private boolean useFingerprint;
    private boolean usePwd;
    private String pwd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person() {
        super();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUseDynamic() {
        return useDynamic;
    }

    public void setUseDynamic(boolean useDynamic) {
        this.useDynamic = useDynamic;
    }

    public boolean isUseFingerprint() {
        return useFingerprint;
    }

    public void setUseFingerprint(boolean useFingerprint) {
        this.useFingerprint = useFingerprint;
    }

    public boolean isUsePwd() {
        return usePwd;
    }

    public void setUsePwd(boolean usePwd) {
        this.usePwd = usePwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
