package com.naskoni.voting.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableRetry
public class WebAppConfig implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    Optional<HttpMessageConverter<?>> jsonConverterOptional =
        converters.stream()
            .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
            .findFirst();

    if (jsonConverterOptional.isPresent()) {
      MappingJackson2HttpMessageConverter converter =
          (MappingJackson2HttpMessageConverter) jsonConverterOptional.get();
      converter.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
  }
}
