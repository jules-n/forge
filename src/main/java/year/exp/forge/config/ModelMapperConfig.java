package year.exp.forge.config;

import lombok.Setter;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import year.exp.forge.domain.*;
import year.exp.forge.domain.User;
import year.exp.forge.dto.*;
import year.exp.forge.services.GcpStorageService;

import java.net.URL;

@Configuration
public class ModelMapperConfig {

    @Setter(onMethod_ = {@Autowired})
    GcpStorageService gcpStorageService;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(userToUserDtoConverter);
        return modelMapper;
    }

    private Converter<User, Registered> userToUserDtoConverter = new AbstractConverter<User, Registered> () {
        protected Registered convert(User user) {
            String link = user.getPhotoLink();
            URL photoLink = link != null ? gcpStorageService.signFile(link) : null;
            if (user.getRole() == Role.MODERATOR) {
                Registered registered = new Moderator();
                registered.setName(user.getName());
                registered.setSurname(user.getSurname());
                registered.setEmail(user.getEmail());
                registered.setRole(user.getRole());
                registered.setPhotoLink(photoLink);
                return registered;
            } else {
                RegisteredUser registered = new RegisteredUser();
                registered.setName(user.getName());
                registered.setSurname(user.getSurname());
                registered.setEmail(user.getEmail());
                registered.setAbout(user.getAbout());
                registered.setContacts(user.getContacts());
                registered.setPhotoLink(photoLink);
                registered.setRole(user.getRole());
                registered.setEducations(user.getEducations());
                registered.setPosition(user.getPosition());
            return registered;
        }}
    };
}
