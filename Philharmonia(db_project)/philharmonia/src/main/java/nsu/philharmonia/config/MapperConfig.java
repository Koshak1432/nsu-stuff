package nsu.philharmonia.config;

import nsu.philharmonia.model.dto.buildings.BuildingDTO;
import nsu.philharmonia.model.entities.buildings.Building;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        TypeMap<Building, BuildingDTO> typeMap = mapper.createTypeMap(Building.class, BuildingDTO.class);
        typeMap.addMappings(m -> m.map(src -> src.getBuildingType().getName(), BuildingDTO::setTypeName));
        return mapper;
    }
}
