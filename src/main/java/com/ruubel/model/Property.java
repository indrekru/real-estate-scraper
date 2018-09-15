package com.ruubel.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Property {

    @Id
    @GeneratedValue
    @NotNull
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private PropertySource source;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "title")
    private String title;

    @Column(name = "rooms")
    private Integer rooms;

    @Column(name = "price")
    private Long price;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "area")
    private Double area;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "notified")
    private Boolean notified;

    @Column(name = "date_created")
    private Instant dateCreated;

    public Property() {
    }

    public Property(PropertySource source, String externalId, String title, Integer rooms, Long price, Integer floor, Double area, Double latitude, Double longitude, Boolean notified) {
        this.source = source;
        this.externalId = externalId;
        this.title = title;
        this.rooms = rooms;
        this.price = price;
        this.floor = floor;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notified = notified;
        this.dateCreated = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public PropertySource getSource() {
        return source;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public Integer getRooms() {
        return rooms;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getFloor() {
        return floor;
    }

    public Double getArea() {
        return area;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
