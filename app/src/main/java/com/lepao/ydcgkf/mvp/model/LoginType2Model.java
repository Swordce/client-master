package com.lepao.ydcgkf.mvp.model;

import java.util.List;

public class LoginType2Model {

    /**
     * errcode : 0
     * errmsg : OK
     * result : {"id":9,"username":"admin","avatar":null,"token":"lepao123456","roleType":1,"venueIdList":[1,2,16,17,18]}
     */

    private int errcode;
    private String errmsg;
    private ResultBean result;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 9
         * username : admin
         * avatar : null
         * token : lepao123456
         * roleType : 1
         * venueIdList : [1,2,16,17,18]
         */

        private int id;
        private String username;
        private Object avatar;
        private String token;
        private int roleType;
        private List<Integer> venueIdList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(Object avatar) {
            this.avatar = avatar;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getRoleType() {
            return roleType;
        }

        public void setRoleType(int roleType) {
            this.roleType = roleType;
        }

        public List<Integer> getVenueIdList() {
            return venueIdList;
        }

        public void setVenueIdList(List<Integer> venueIdList) {
            this.venueIdList = venueIdList;
        }
    }
}
