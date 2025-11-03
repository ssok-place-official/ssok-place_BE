package com.example.ssokPlace.list.entity;

import com.example.ssokPlace.places.entity.Place;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "place_list_place")
public class PlaceListPlace {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리스트 FK
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_place_list_place_list"))
    private PlaceList list;

    // 장소 FK
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_place_list_place_place"))
    private Place place;

    public static PlaceListPlace link(PlaceList list, Place place) {
        PlaceListPlace link = PlaceListPlace.builder()
                .list(list)
                .place(place)
                .build();
        list.addPlace(link);
        return link;
    }

}
