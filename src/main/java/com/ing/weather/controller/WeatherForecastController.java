package com.ing.weather.controller;

import com.ing.weather.dto.WeatherForecastDto;
import com.ing.weather.service.WeatherForecastService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/weather-forecast")
public class WeatherForecastController {
    private final WeatherForecastService weatherForecastService;

    public WeatherForecastController(WeatherForecastService weatherForecastService) {
        this.weatherForecastService = weatherForecastService;
    }

    @GetMapping("/{city}")
    public ResponseEntity<List<WeatherForecastDto>> getTwoDaysWeatherForecast(@PathVariable("city") @NotBlank String city){
        return ResponseEntity.ok(weatherForecastService.getTwoDaysWeatherForecast(city));
    }
}
