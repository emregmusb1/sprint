package com.liying.app.update;

public class Result {
    private Integer code;
    private String data;
    private String message;

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code2) {
        this.code = code2;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data2) {
        this.data = data2;
    }

    public String toString() {
        return "Result{code=" + this.code + ", message='" + this.message + '\'' + ", data='" + this.data + '\'' + '}';
    }
}
