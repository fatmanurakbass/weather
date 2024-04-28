package com.ing.weather.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.weather.dto.WeatherForecastDto;
import com.ing.weather.mapper.WeatherForecastMapper;
import com.ing.weather.model.City;
import com.ing.weather.model.WeatherForecast;
import com.ing.weather.model.response.WeatherEntry;
import com.ing.weather.model.response.WeatherResponse;
import com.ing.weather.repository.CityRepository;
import com.ing.weather.repository.WeatherForecastRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherForecastService {

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.url}")
    private String apiUrl;
    private final WeatherForecastRepository weatherForecastRepository;
    private final WeatherForecastMapper weatherForecastMapper;
    private final CityRepository cityRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherForecastService(WeatherForecastRepository weatherForecastRepository, WeatherForecastMapper weatherForecastMapper, CityRepository cityRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.weatherForecastRepository = weatherForecastRepository;
        this.weatherForecastMapper = weatherForecastMapper;
        this.cityRepository = cityRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<WeatherForecastDto> getTwoDaysWeatherForecast(String cityName){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(48);
        List<WeatherForecast> weatherForecastList = weatherForecastRepository.findByCity_NameAndForecastTimeBetweenOrderByForecastTimeAsc(cityName, startTime, endTime);

        if(weatherForecastList.isEmpty()){
            return weatherForecastMapper.modelsToDtos(getWeatherForecastFromOpenWeather(cityName));
        }
        return weatherForecastMapper.modelsToDtos(weatherForecastList);
    }

    private List<WeatherForecast> getWeatherForecastFromOpenWeather(String cityName){
        try {
            String url = apiUrl.replace("{city}", cityName)
                               .replace("{appid}", apiKey);

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
            return saveWeatherData(weatherResponse);
        }
        catch (Exception exception){
            throw new RuntimeException();
        }
    }

    @Transactional
    public List<WeatherForecast> saveWeatherData(WeatherResponse response) {

        City city = new City();
        city.setName(response.getCity().getName());
        city = cityRepository.save(city);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<WeatherForecast> weatherForecastList = new ArrayList<>();
        for (WeatherEntry data : response.getList()) {
            WeatherForecast forecast = new WeatherForecast();
            forecast.setCity(city);
            forecast.setFeelsLike(data.getMain().getFeelsLike());
            forecast.setTempMax(data.getMain().getTempMax());
            forecast.setHumidity(data.getMain().getHumidity());
            forecast.setForecastTime(LocalDateTime.parse(data.getDtTxt(), dateTimeFormatter));
            weatherForecastList.add(forecast);
        }
        weatherForecastRepository.saveAll(weatherForecastList);
        return weatherForecastList.stream()
                .limit(16)
                .collect(Collectors.toList());
    }
}
