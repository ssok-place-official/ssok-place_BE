package com.example.ssokPlace.places.entity;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;

@Entity
@Table(name = "place")
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

    // 실제 좌표 공간 데이터를 공간 타입(Point)로 저장하는 MySQL Spatial 컬럼
    // SRID 4236 = WGS84 좌표계
    @Column(columnDefinition = "point SRID 4326 not null")
    private Point ego;
}
