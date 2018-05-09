package cn.com.larunda.safebox.recycler;

public class Destination {
    private int id;
    private String originCity;
    private String destinationCity;
    private String person;
    private String startTime;
    private String endTime;
    private String dynamic;
    private String useLeaving;
    private String useDefence;
    private String interval;
    private String area;

    public String getUseLeaving() {
        return useLeaving;
    }

    public void setUseLeaving(String useLeaving) {
        this.useLeaving = useLeaving;
    }

    public String getUseDefence() {
        return useDefence;
    }

    public void setUseDefence(String useDefence) {
        this.useDefence = useDefence;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public Destination() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
