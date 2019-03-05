package com.lepao.ydcgkf.mvp.model;

/**
 * created by zwj on 2018/9/6 0006
 */
public class UserInfoModel {

    /**
     * code : 200
     * msg : 查询成功
     * data : {"serialNumber":"800012340001","mobile":"13552939199","username":"奥巴马","gender":"男","birthday":"1961-08-04","address":"华盛顿特区宜宾法尼亚大道1创沁号"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * serialNumber : 800012340001
         * mobile : 13552939199
         * username : 奥巴马
         * gender : 男
         * birthday : 1961-08-04
         * address : 华盛顿特区宜宾法尼亚大道1创沁号
         */

        private String serialNumber;
        private String mobile;
        private String username;
        private String gender;
        private String birthday;
        private String address;

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
