package com.locifierapp.locifier.map;
import android.location.Location;

import androidx.annotation.NonNull;

import java.sql.SQLOutput;
import java.util.Map;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

public class AreaOnMap {

    private final double LONGITUDE_IN_MILES = 69;
    private final double METER_IN_MILES = 0.000621371192;
    private final double AREA_RADIUS_IN_METERS = 500;

    private Location centralCoordinate;
    private Location areaLeftTopCoordinates;
    private Location areaRightTopCoordinates;
    private Location areaRightBottomCoordinates;
    private Location areaLeftBottomCoordinates;

    public double latDifferenceInDegrees;
    public double lonDifferenceInDegrees;

    public AreaOnMap(Location location){
        this.centralCoordinate = location;
        this.latDifferenceInDegrees = calculateLatDifferenceFromRadius();
        this.lonDifferenceInDegrees = calculateLonDifferenceFromRadius();
        setAreaCoordinates();


    }

    private double calculateLatDifferenceFromRadius() {
        double differenceInDegrees = (AREA_RADIUS_IN_METERS*METER_IN_MILES) / 69;
        return differenceInDegrees;
    }

    private double calculateLonDifferenceFromRadius() {
        if (this.centralCoordinate != null) {
            double latDifference = (AREA_RADIUS_IN_METERS*METER_IN_MILES) / 69;
            double latDegrees = this.centralCoordinate.getLatitude();
            double differenceInDegrees = latDifference / cos(toRadians(latDegrees));
            return differenceInDegrees;
        }
        return 0.0;
    }

    private double calculateSouthernMostCoordinate(){
        return centralCoordinate.getLatitude() - latDifferenceInDegrees;
    }

    private double calculateNorthernMostCoordinate(){
        return centralCoordinate.getLatitude() + latDifferenceInDegrees;
    }

    private double calculateWesternMostCoordinate(){
        return centralCoordinate.getLongitude() - lonDifferenceInDegrees;
    }

    private double calculateEasternMostCoordinate(){
        return centralCoordinate.getLongitude() + lonDifferenceInDegrees;
    }

    private void setAreaCoordinates(){
        this.areaLeftBottomCoordinates = new Location("leftBottomOfArea");
        this.areaLeftBottomCoordinates.setLongitude(calculateSouthernMostCoordinate());
        this.areaLeftBottomCoordinates.setLatitude(calculateWesternMostCoordinate());

        this.areaLeftTopCoordinates = new Location("leftTopOfArea");
        this.areaLeftTopCoordinates.setLongitude(calculateNorthernMostCoordinate());
        this.areaLeftTopCoordinates.setLatitude(calculateWesternMostCoordinate());

        this.areaRightTopCoordinates = new Location("rightTopOfArea");
        this.areaRightTopCoordinates.setLongitude(calculateNorthernMostCoordinate());
        this.areaRightTopCoordinates.setLatitude(calculateEasternMostCoordinate());

        this.areaRightBottomCoordinates = new Location("leftBottomOfArea");
        this.areaRightBottomCoordinates.setLongitude(calculateSouthernMostCoordinate());
        this.areaRightBottomCoordinates.setLatitude(calculateEasternMostCoordinate());
    }

    public Location getAreaLeftTopCoordinates() {
        return areaLeftTopCoordinates;
    }

    public Location getAreaRightTopCoordinates() {
        return areaRightTopCoordinates;
    }

    public Location getAreaRightBottomCoordinates() {
        return areaRightBottomCoordinates;
    }

    public Location getAreaLeftBottomCoordinates() {
        return areaLeftBottomCoordinates;
    }


    public void print(){
        System.out.println("Lat: " + areaLeftTopCoordinates.getLatitude() + "    Lon: " + areaLeftTopCoordinates.getLongitude());
    }
}
