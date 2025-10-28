package com.example.ssokPlace.places.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "places")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length= 120)
    private String name;

    private String address;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    // Naver 맵 API 불러와서 JSON으로 저장하는 필드
    @Column(columnDefinition = "json")
    private String externalRefs;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "ego", columnDefinition = "POINT SRID 4326 NOT NULL", nullable = false)
    private Point ego;

    @Column(name = "cover_url")
    private String coverUrl;

}
