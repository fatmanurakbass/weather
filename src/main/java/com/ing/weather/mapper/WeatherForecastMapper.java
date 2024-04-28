package com.ing.weather.mapper;

import com.ing.weather.dto.WeatherForecastDto;
import com.ing.weather.model.WeatherForecast;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WeatherForecastMapper {

    WeatherForecastMapper INSTANCE = Mappers.getMapper( WeatherForecastMapper.class );

    List<WeatherForecastDto> modelsToDtos(List<WeatherForecast> weatherForecasts);

    List<WeatherForecast> dtosToModels(List<WeatherForecastDto> weatherForecast);

    WeatherForecastDto modelToDto(WeatherForecast weatherForecast);

}
