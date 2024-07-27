package com.github.k2ocabhinav.ubercloneapp.configs;

import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.utils.GeometryUtil;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(PointDto.class, Point.class).setConverter(converter -> {
            PointDto pointDto = converter.getSource();
            return GeometryUtil.createPoint(pointDto);
        });
        return mapper;
    }
}

/*
The ModelMapper instance is configured with a custom converter to handle PointDto to Point conversions.
 When modelMapper.map(pointDto, Point.class) is called, the custom converter is triggered.
 The converter takes the PointDto object and passes it to the GeometryUtil.createPoint method.
 The GeometryUtil.createPoint method:
     Extracts the coordinates from the PointDto.
     Creates a Coordinate object with these coordinates.
     Uses the GeometryFactory to create a Point object from the Coordinate.
 The resulting Point object is returned by the converter and assigned to the point variable.
 */
