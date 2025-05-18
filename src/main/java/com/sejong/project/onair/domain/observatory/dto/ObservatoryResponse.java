package com.sejong.project.onair.domain.observatory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.graphbuilder.geom.Geom;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ObservatoryResponse {

    public record UpdateDto(
            List<Observatory> newObservatory,
            List<Observatory> deletedObservatory
    ){}


    public record Geometry(
            String type,
            @JsonFormat(shape = JsonFormat.Shape.ARRAY)
            double[] coordinates
    ){
        public static Geometry to (Observatory observatory){
            return new Geometry(
                "Point",
                    new double[]{ observatory.getDmY(), observatory.getDmX() }
            );
        }
    }

    public record LocationDto(
            double dmX,
            double dmY
    ){
        public static LocationDto to (Observatory observatory){
            return new LocationDto(
                    observatory.getDmX(),
                    observatory.getDmY()
            );
        }
    }

    public record Properties(
            String stationName
            //note 추가로 필요한 정보가능
    ){
        public static Properties to(Observatory observatory){
            return new Properties(
                    observatory.getStationName()
            );
        }
    }

    public record ObservatoryDto(
            String type,
            String id,
            Geometry geometry,
            Properties properties
    ){
        public static ObservatoryDto to (Observatory observatory){
            return new ObservatoryDto(
                    "Feature",
                    observatory.getStationName(),
                    Geometry.to(observatory),
                    Properties.to(observatory)
            );
        }

        public static List<ObservatoryDto> toAll (List<Observatory> observatories){
            List<ObservatoryDto> dtos = new ArrayList<>();
            for(Observatory ob : observatories){
                dtos.add(new ObservatoryDto(
                        "Feature",
                        ob.getStationName(),
                        Geometry.to(ob),
                        Properties.to(ob)
                ));
            }
            return dtos;
        }
    }

    public record FeatureCollectionDto(
            String type,
            ObservatoryDto features
    ){
        public static FeatureCollectionDto to(Observatory observatory){
            return new FeatureCollectionDto(
                    "FeatureCollection",
                    ObservatoryDto.to(observatory)
                    );
        }

        public static List<FeatureCollectionDto> toAll(List<Observatory> observatories){
            List<FeatureCollectionDto> dtos = new ArrayList<>();
            for(Observatory  ob: observatories){
                dtos.add(
                        new FeatureCollectionDto(
                                "FeatureCollection",
                                ObservatoryDto.to(ob)
                        )
                );
            }
            return dtos;
        }
    }

}
