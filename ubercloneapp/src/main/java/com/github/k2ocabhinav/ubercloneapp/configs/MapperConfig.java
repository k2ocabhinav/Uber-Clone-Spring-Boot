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

//      From PointDto to Point obj
        mapper.typeMap(PointDto.class, Point.class).setConverter(context -> {
            PointDto pointDto = context.getSource();
            return GeometryUtil.createPoint(pointDto);
        });

//      From Point obj to PointDto
        mapper.typeMap(Point.class, PointDto.class).setConverter(context -> {
            Point point = context.getSource();
            double[] coordinates = {
                    point.getX(),
                    point.getY()
            };
            return new PointDto(coordinates);
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
