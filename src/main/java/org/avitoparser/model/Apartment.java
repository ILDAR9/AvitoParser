package org.avitoparser.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "Apartment", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ap_address")})
public class Apartment {

    @Id
    @GeneratedValue
    @Column(name = "ap_id")
    private Long id;

    @NotNull
    @Column(name = "ap_area")
    private int area;

    @NotNull
    @Column(name = "ap_room_count")
    private int roomCount;

    @Column(name = "ap_floor_count")
    private int floorCount;

    @Column(name = "ap_floor_current")
    private int currentFloor;

    @NotNull
    @Column(name = "ap_address", unique = true)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ap_city_id", referencedColumnName = "city_id", nullable = false)
    private City city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public int getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(int floorCount) {
        this.floorCount = floorCount;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ap_hs_type", referencedColumnName = "hs_type_id", nullable = false)
    private HouseType houseType;

}
