package com.sipsd.flow.bean;


import cn.hutool.db.Session;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class Client implements Serializable {

    private static final long serialVersionUID = 8957107006902627635L;

    private String userName;

    private Session session;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Client(String userName, Session session) {
        this.userName = userName;
        this.session = session;
    }

    public Client() {
    }

    @Override
    public int hashCode() {
        return  Objects.hash(userName,session);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            Client c = (Client) obj;
            return super.equals(obj) && userName == c.userName;
        }
        return super.equals(obj);
    }

}
