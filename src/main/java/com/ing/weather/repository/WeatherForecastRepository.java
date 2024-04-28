package com.ing.weather.repository;

import com.ing.weather.model.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    @Query("SELECT wf FROM WeatherForecast wf WHERE wf.city.name = :cityName AND wf.forecastTime BETWEEN :start AND :end ORDER BY wf.forecastTime ASC")
    List<WeatherForecast> findByCity_NameAndForecastTimeBetweenOrderByForecastTimeAsc(
            @Param("cityName") String cityName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
