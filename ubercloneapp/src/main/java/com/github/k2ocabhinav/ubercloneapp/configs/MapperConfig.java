package com.github.k2ocabhinav.ubercloneapp.configs;

import com.github.k2ocabhinav.ubercloneapp.dto.PointDto;
import com.github.k2ocabhinav.ubercloneapp.dto.SignupDto;
import com.github.k2ocabhinav.ubercloneapp.dto.UserDto;
import com.github.k2ocabhinav.ubercloneapp.entities.User;
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
        mapper.getConfiguration().setAmbiguityIgnored(true);

//      From PointDto to Point obj
        mapper.typeMap(PointDto.class, Point.class).setConverter(context -> {
            PointDto pointDto = context.getSource();
            if (pointDto == null) {
                return null;
            }
            return GeometryUtil.createPoint(pointDto);
        });

//      From Point obj to PointDto
        mapper.typeMap(Point.class, PointDto.class).setConverter(context -> {
            Point point = context.getSource();
            if (point == null) {
                return null;
            }
            double[] coordinates = {
                    point.getX(),
                    point.getY()
            };
            return new PointDto(coordinates);
        });

        mapper.typeMap(SignupDto.class, User.class).addMappings(mapping -> {
            mapping.skip(User::setFirstName);
            mapping.skip(User::setLastName);
        }).setPostConverter(context -> {
            SignupDto source = context.getSource();
            User destination = context.getDestination();
            String[] nameParts = source.getName() == null ? new String[0] : source.getName().trim().split("\\s+", 2);

            if (nameParts.length > 0) {
                destination.setFirstName(nameParts[0]);
            }
            if (nameParts.length > 1) {
                destination.setLastName(nameParts[1]);
            }

            return destination;
        });

        mapper.typeMap(User.class, UserDto.class).addMappings(mapping -> mapping.skip(UserDto::setName))
                .setPostConverter(context -> {
                    User source = context.getSource();
                    UserDto destination = context.getDestination();
                    String firstName = source.getFirstName() == null ? "" : source.getFirstName().trim();
                    String lastName = source.getLastName() == null ? "" : source.getLastName().trim();
                    String fullName = (firstName + " " + lastName).trim();

                    destination.setName(fullName.isEmpty() ? null : fullName);
                    return destination;
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
