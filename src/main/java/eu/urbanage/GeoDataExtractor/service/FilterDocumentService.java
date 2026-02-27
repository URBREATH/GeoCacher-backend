package eu.urbanage.GeoDataExtractor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.urbanage.GeoDataExtractor.model.FilterDetail;
import eu.urbanage.GeoDataExtractor.model.FilterDocument;
import eu.urbanage.GeoDataExtractor.repository.FilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FilterDocumentService {

    private static final Logger log = LoggerFactory.getLogger(FilterDocumentService.class);

    @Autowired
    protected FilterRepository fRepo;

    @Autowired
    protected FilterService fs;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.cities}")
    private List<String> cityList;

    private final Map<String, String> iconCache = new ConcurrentHashMap<>();


    public ResponseEntity<FilterDetail> findFilterByCity(String city) {

        try {
            List<FilterDocument> foundFilters = fRepo.findBycityName(city);

            if (foundFilters.isEmpty()) {
                log.warn("No filters found in cache for city: {}", city);
                return ResponseEntity.notFound().build();
            }

            FilterDocument filterDocument = foundFilters.get(0); // Assuming one document per city

            List<String> mainFilters = filterDocument.getFilter();
            List<String> detailFilters = filterDocument.getDetailFilter();

            FilterDetail responseFilter = new FilterDetail();
            responseFilter.setFilter(mainFilters);
            responseFilter.setFilter_detail(detailFilters);

            return ResponseEntity.ok().body(responseFilter);

        } catch (IndexOutOfBoundsException e) {
            log.error("Data inconsistency found for city: {}. More than one filter document might exist.", city, e);
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            log.error("Error retrieving filters for city {}: {}", city, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public void updateFilter(List<String> filter_list, String cityName) {
        // Find the first document for the city, assuming there's only one.
        FilterDocument filterDocument = fRepo.findBycityName(cityName)
                .stream()
                .findFirst()
                .orElse(new FilterDocument()); // Or create a new one if it doesn't exist

        filterDocument.setCityName(cityName);
        filterDocument.setFilter(filter_list);
        
        // ---> IMPROVED LOGIC: Iterate over all main_filters and aggregate their detail_filters <---
        List<String> allDetailFilters = new ArrayList<>();
        if (!filter_list.isEmpty()) {
            for (String mainFilter : filter_list) {
                List<String> foundDetailFilters = fs.getEntityAttributes(mainFilter, cityName);
                allDetailFilters.addAll(foundDetailFilters);
            }
        }
        // Remove duplicates and save
        filterDocument.setDetailFilter(allDetailFilters.stream().distinct().collect(Collectors.toList()));

        fRepo.save(filterDocument);
        log.info("Updated main filters for city {}: {}", cityName, filter_list);
        log.info("Updated detail filters for city {}: {}", cityName, filterDocument.getDetailFilter());
    }

    public void checkFilter() throws JsonProcessingException {
        log.info("Starting scheduled filter check for all configured cities...");
        List<String> city_list = getCityList();

        if (city_list.isEmpty()) {
            log.warn("City list is empty. No filters will be checked. Check 'app.cities' configuration.");
            return;
        }

        for (String city : city_list) {

            List<String> filter_list = fs.getAllCityFilter(city);

            updateFilter(filter_list, city);

        }
        log.info("Finished scheduled filter check.");
    }

    public ResponseEntity<List<Map<String, Object>>> retriveFilterList(String city) {
        try {
            List<FilterDocument> foundFilters = fRepo.findBycityName(city);

            if (foundFilters.isEmpty()) {
                log.warn("No filters found in cache for city: {}", city);
                return ResponseEntity.notFound().build();
            }

            FilterDocument filterDocument = foundFilters.get(0); // Assuming one document per city

            List<Map<String, Object>> response = new ArrayList<>();
            List<String> mainFilters = filterDocument.getFilter();

            for (String mainFilter : mainFilters) {
                Map<String, Object> filterGroup = new HashMap<>();
                filterGroup.put("main_filter", List.of(mainFilter));

                List<String> detailFilters = fs.getEntityAttributes(mainFilter, city);
                List<Object[]> detailFiltersWithIcons = detailFilters.stream()
                        .map(name -> new Object[]{name, getIconUrl(name)})
                        .collect(Collectors.toList());
                filterGroup.put("detail_filter", detailFiltersWithIcons);
                response.add(filterGroup);
            }

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            log.error("Error retrieving structured filters for city {}: {}", city, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getIconUrl(String name) {
        return iconCache.computeIfAbsent(name, n -> {
            try {
                String iconifyUrl = "https://api.iconify.design/lucide/" + n + ".svg";
                // We don't need to download the SVG content, just the URL is fine.
                // If validation is needed, a HEAD request would be more efficient.
                return iconifyUrl;
            } catch (Exception e) {
                log.error("Failed to retrieve icon for {}", n, e);
                return ""; // Return empty string on failure
            }
        });
    }

    private List<String> getCityList() {
        // The list is now injected from application.properties
        return this.cityList;
    }

}
