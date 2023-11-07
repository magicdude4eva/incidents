package com.example.incidents.mapper;

import com.example.incidents.data.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Mapper
public interface DataToESMapper {

    DataToESMapper INSTANCE = Mappers.getMapper(DataToESMapper.class);

    @Mapping(source = "lat", target = "latitude")
    @Mapping(source = "lon", target = "longitude")
    GeoPoint locationToGeoPoint(Location location);

    com.example.incidents.es.Incident dataIncidentToES(com.example.incidents.data.Incident incident);

    com.example.incidents.data.Incident esIncidentToData(com.example.incidents.es.Incident incident);

    com.example.incidents.data.SearchResult esSearchResultToData(com.example.incidents.es.SearchResult searchResult);
}
