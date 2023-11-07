package com.example.incidents.api;

import com.example.incidents.mapper.ApiToDataMapper;
import com.example.incidents.service.IncidentService;
import com.example.incidents.service.TechnicalException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsApi {

    @Autowired
    IncidentService incidentService;

    @PutMapping(
            value = "/incidents/log",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LogResponse log(@Valid @RequestBody LogRequest logRequest) {
        final var incident = ApiToDataMapper.INSTANCE.logRequestToIncident(logRequest);

        try {
            final var id = incidentService.logIncident(incident);
            return new LogResponse(id);
        } catch (TechnicalException technicalException) {
            throw new InternalServerError("Technical error in \"/incidents/log\"", technicalException);
        }
    }

//    @PostMapping(
//            value = "/incidents/search",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public SearchResponse search(@Valid @RequestBody SearchRequest searchRequest) {
//        final var incidentSearchCriteria = ApiToDataMapper.INSTANCE.searchRequestToCriteria(searchRequest);
//
//        try {
//            incidentService.search(incidentSearchCriteria);
//        } catch (TechnicalException technicalException) {
//
//        }
//    }
}
