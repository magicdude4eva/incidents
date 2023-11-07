package com.example.incidents.mapper;

import com.example.incidents.api.LogRequest;
import com.example.incidents.api.SearchRequest;
import com.example.incidents.api.SearchResponseItem;
import com.example.incidents.data.Incident;
import com.example.incidents.data.SearchCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApiToDataMapper {

    ApiToDataMapper INSTANCE = Mappers.getMapper(ApiToDataMapper.class);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    Incident logRequestToIncident(LogRequest logRequest);

    SearchCriteria searchRequestToCriteria(SearchRequest searchRequest);

    SearchResponseItem incidentToSearchResponse(Incident incident);
}
