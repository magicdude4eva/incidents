package com.example.incidents.mapper;

import com.example.incidents.service.Location;
import com.example.incidents.service.Incident;
import com.example.incidents.service.SearchResult;
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

    com.example.incidents.es.Incident dataIncidentToES(Incident incident);

    Incident esIncidentToData(com.example.incidents.es.Incident incident);

    SearchResult esSearchResultToData(com.example.incidents.es.SearchResult searchResult);
}
