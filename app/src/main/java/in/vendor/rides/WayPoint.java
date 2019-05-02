package in.vendor.rides;


public class WayPoint {

    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getLatLang() {
        return latLang;
    }

    public void setLatLang(String latLang) {
        this.latLang = latLang;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String latLang = null;
     private String address = null;


}
