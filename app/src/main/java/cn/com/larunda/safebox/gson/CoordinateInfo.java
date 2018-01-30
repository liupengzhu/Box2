package cn.com.larunda.safebox.gson;

import java.util.List;

/**
 * Created by sddt on 18-1-30.
 */

public class CoordinateInfo {

    /**
     * id : 291
     * f_name : 南京大学
     * f_data : [[{"lng":"118.782898","lat":"32.056803"},{"lng":"118.786204","lat":"32.056405"},{"lng":"118.790462","lat":"32.056528"},{"lng":"118.790228","lat":"32.064928"},{"lng":"118.781371","lat":"32.06505"},{"lng":"118.780922","lat":"32.060628"}]]
     * f_is_manual : 1
     */

    private int id;
    private String f_name;
    private String f_is_manual;
    private List<List<FDataBean>> f_data;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_is_manual() {
        return f_is_manual;
    }

    public void setF_is_manual(String f_is_manual) {
        this.f_is_manual = f_is_manual;
    }

    public List<List<FDataBean>> getF_data() {
        return f_data;
    }

    public void setF_data(List<List<FDataBean>> f_data) {
        this.f_data = f_data;
    }

    public static class FDataBean {
        /**
         * lng : 118.782898
         * lat : 32.056803
         */

        private String lng;
        private String lat;

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }
}
