package com.example.incidents.api;

import com.example.incidents.mapper.ApiToDataMapper;
import com.example.incidents.service.IncidentsService;
import com.example.incidents.service.ServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsApi {

    @Autowired
    IncidentsService incidentsService;

    /**
     * Log (i.e. create) a new incident. For the new item a UUID will be created as ID.
     * This is the endpoint's response.
     *
     * @param logRequest the new incident to be created
     *
     * @return UUID of newly created incident
     *
     * @throws ServiceException will be mapped to HTTP-code IncidentExceptionController
     */
    @PutMapping(
            value = "/incidents/log",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public LogResponse log(@Valid @RequestBody LogRequest logRequest) throws ServiceException {
        final var incident = ApiToDataMapper.INSTANCE.logRequestToIncident(logRequest);
        final var id = incidentsService.logIncident(incident);
        return new LogResponse(id);
    }

    /**
     * Search incidents based on "searchRequest"
     *
     * @param searchRequest contains search parameters
     *
     * @return a page of the search result holding total number of matches, number of matches in current page,
     * and the matches (i.e. incidents)
     *
     * @throws ServiceException will be mapped to HTTP-code IncidentExceptionController
     */
    @PostMapping(
            value = "/incidents/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public SearchResponse search(@Valid @RequestBody SearchRequest searchRequest) throws ServiceException {
        final var mapper = ApiToDataMapper.INSTANCE;
        final var incidentSearchCriteria = mapper.searchRequestToCriteria(searchRequest);
        return mapper.searchResultToResponse(incidentsService.search(incidentSearchCriteria));
    }
}
