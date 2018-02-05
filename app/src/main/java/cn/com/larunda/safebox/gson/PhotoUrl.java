package cn.com.larunda.safebox.gson;

/**
 * Created by sddt on 18-2-5.
 */

public class PhotoUrl {

    /**
     * status : 413
     * message : 请上传小于2M的文件
     */

    private int status;
    private String message;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
