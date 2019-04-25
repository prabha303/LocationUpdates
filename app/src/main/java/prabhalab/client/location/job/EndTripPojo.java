package prabhalab.client.location.job;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import prabhalab.client.location.JrWayDao;
import prabhalab.client.location.Utility;
import prabhalab.client.location.WayPoint;

public class EndTripPojo {

    private String  jobID = "";
    private String  tripTime = "";
    private String  pickupLatlng = "";
    private String  pickupTime = "";
    private String  pickup_address = "";
    private String  drop_location = "";
    private String  dropTime = "";
    private String  drop_address = "";
    //private ArrayList<WayPoint> wayPointList ;
    private String wayPointList ;
    private String startingKm = "1";
    private String totalpickupKM  = "";
    private String totalDropKM  = "";
    private String getTotalKM  = "";
    private  String  journeyEndTime = "";
    private String finishingKm = "" ;
    private String  waitingExtras = "";
    private String  parkingExtras = "";
    private String  tollsExtras = "";
    private  String  amendmentExtras = "";
    private  String  phoneExtras = "";
    private   String  othersExtras = "";
    private   String  serviceChargeExtras = "";
    private   String  note = "";
    private   String  cancellationExtras = "";
    private   String  TravellerSignature = "";
    private    String passengerOnBoardTime  = "";
    private   String  journeyStartTime = "";
    private  String  passengerDropOffTime = "";
    private    String  isCheckedFixedRate = "";
    private   String  SelectedFixedPrice = "";
    private   String  panLocationID = "";
    private  String  isairportornormal = "";
    private  String  breaksRating = "";
    private   String  overSpeedRating = "";
    private  String  trafficviolateRating = "";
    private  String  phoneWhileDriveRating = "";

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getPickupLatlng() {
        return pickupLatlng;
    }

    public void setPickupLatlng(String pickupLatlng) {
        this.pickupLatlng = pickupLatlng;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public String getDropTime() {
        return dropTime;
    }

    public void setDropTime(String dropTime) {
        this.dropTime = dropTime;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getWayPointList() {
        return wayPointList;
    }

    public void setWayPointList(String  wayPointList) {
        this.wayPointList = wayPointList;
    }

    public String getStartingKm() {
        return startingKm;
    }

    public void setStartingKm(String startingKm) {
        this.startingKm = startingKm;
    }

    public String getTotalpickupKM() {
        return totalpickupKM;
    }

    public void setTotalpickupKM(String totalpickupKM) {
        this.totalpickupKM = totalpickupKM;
    }

    public String getTotalDropKM() {
        return totalDropKM;
    }

    public void setTotalDropKM(String totalDropKM) {
        this.totalDropKM = totalDropKM;
    }

    public String getGetTotalKM() {
        return getTotalKM;
    }

    public void setGetTotalKM(String getTotalKM) {
        this.getTotalKM = getTotalKM;
    }

    public String getJourneyEndTime() {
        return journeyEndTime;
    }

    public void setJourneyEndTime(String journeyEndTime) {
        this.journeyEndTime = journeyEndTime;
    }

    public String getFinishingKm() {
        return finishingKm;
    }

    public void setFinishingKm(String finishingKm) {
        this.finishingKm = finishingKm;
    }

    public String getWaitingExtras() {
        return waitingExtras;
    }

    public void setWaitingExtras(String waitingExtras) {
        this.waitingExtras = waitingExtras;
    }

    public String getParkingExtras() {
        return parkingExtras;
    }

    public void setParkingExtras(String parkingExtras) {
        this.parkingExtras = parkingExtras;
    }

    public String getTollsExtras() {
        return tollsExtras;
    }

    public void setTollsExtras(String tollsExtras) {
        this.tollsExtras = tollsExtras;
    }

    public String getAmendmentExtras() {
        return amendmentExtras;
    }

    public void setAmendmentExtras(String amendmentExtras) {
        this.amendmentExtras = amendmentExtras;
    }

    public String getPhoneExtras() {
        return phoneExtras;
    }

    public void setPhoneExtras(String phoneExtras) {
        this.phoneExtras = phoneExtras;
    }

    public String getOthersExtras() {
        return othersExtras;
    }

    public void setOthersExtras(String othersExtras) {
        this.othersExtras = othersExtras;
    }

    public String getServiceChargeExtras() {
        return serviceChargeExtras;
    }

    public void setServiceChargeExtras(String serviceChargeExtras) {
        this.serviceChargeExtras = serviceChargeExtras;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCancellationExtras() {
        return cancellationExtras;
    }

    public void setCancellationExtras(String cancellationExtras) {
        this.cancellationExtras = cancellationExtras;
    }

    public String getTravellerSignature() {
        return TravellerSignature;
    }

    public void setTravellerSignature(String travellerSignature) {
        TravellerSignature = travellerSignature;
    }

    public String getPassengerOnBoardTime() {
        return passengerOnBoardTime;
    }

    public void setPassengerOnBoardTime(String passengerOnBoardTime) {
        this.passengerOnBoardTime = passengerOnBoardTime;
    }

    public String getJourneyStartTime() {
        return journeyStartTime;
    }

    public void setJourneyStartTime(String journeyStartTime) {
        this.journeyStartTime = journeyStartTime;
    }

    public String getPassengerDropOffTime() {
        return passengerDropOffTime;
    }

    public void setPassengerDropOffTime(String passengerDropOffTime) {
        this.passengerDropOffTime = passengerDropOffTime;
    }

    public String getIsCheckedFixedRate() {
        return isCheckedFixedRate;
    }

    public void setIsCheckedFixedRate(String isCheckedFixedRate) {
        this.isCheckedFixedRate = isCheckedFixedRate;
    }

    public String getSelectedFixedPrice() {
        return SelectedFixedPrice;
    }

    public void setSelectedFixedPrice(String selectedFixedPrice) {
        SelectedFixedPrice = selectedFixedPrice;
    }

    public String getPanLocationID() {
        return panLocationID;
    }

    public void setPanLocationID(String panLocationID) {
        this.panLocationID = panLocationID;
    }

    public String getIsairportornormal() {
        return isairportornormal;
    }

    public void setIsairportornormal(String isairportornormal) {
        this.isairportornormal = isairportornormal;
    }

    public String getBreaksRating() {
        return breaksRating;
    }

    public void setBreaksRating(String breaksRating) {
        this.breaksRating = breaksRating;
    }

    public String getOverSpeedRating() {
        return overSpeedRating;
    }

    public void setOverSpeedRating(String overSpeedRating) {
        this.overSpeedRating = overSpeedRating;
    }

    public String getTrafficviolateRating() {
        return trafficviolateRating;
    }

    public void setTrafficviolateRating(String trafficviolateRating) {
        this.trafficviolateRating = trafficviolateRating;
    }

    public String getPhoneWhileDriveRating() {
        return phoneWhileDriveRating;
    }

    public void setPhoneWhileDriveRating(String phoneWhileDriveRating) {
        this.phoneWhileDriveRating = phoneWhileDriveRating;
    }



}
