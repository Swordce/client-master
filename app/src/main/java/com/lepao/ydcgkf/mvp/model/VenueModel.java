package com.lepao.ydcgkf.mvp.model;

import java.util.List;

public class VenueModel {

    /**
     * errcode : 0
     * errmsg : OK
     * result : [{"id":1,"name":"合肥市稻香村小学","phonecall":"2822128","lng":31.835035,"lat":117.232867,"createtime":1538291041000,"district":1},{"id":2,"name":"合肥市第三十四中学","phonecall":"65563452","lng":31.8589633609,"lat":117.2378643419,"createtime":1538291041000,"district":1}]
     */

    private int errcode;
    private String errmsg;
    private List<ResultBean> result;

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

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : 1
         * name : 合肥市稻香村小学
         * phonecall : 2822128
         * lng : 31.835035
         * lat : 117.232867
         * createtime : 1538291041000
         * district : 1
         */

        private int id;
        private String name;
        private String phonecall;
        private double lng;
        private double lat;
        private long createtime;
        private int district;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhonecall() {
            return phonecall;
        }

        public void setPhonecall(String phonecall) {
            this.phonecall = phonecall;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public int getDistrict() {
            return district;
        }

        public void setDistrict(int district) {
            this.district = district;
        }
    }
}
