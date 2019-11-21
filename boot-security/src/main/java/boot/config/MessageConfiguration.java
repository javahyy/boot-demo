package boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import net.dreamlu.mica.core.utils.Charsets;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 如果出现低版本ie json下载的情况配置
 */
@Configuration
@AllArgsConstructor
public class MessageConfiguration implements WebMvcConfigurer {
	private final ObjectMapper objectMapper;

	/**
	 * 消息转换
	 *
	 * @param converters 转换器
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.clear();
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new StringHttpMessageConverter(Charsets.UTF_8));
		converters.add(new ResourceHttpMessageConverter());
		converters.add(new ResourceRegionHttpMessageConverter());
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
		// 如果出现低版本ie json下载的
		List<MediaType> supportedMediaTypes = new ArrayList<>(2);
		supportedMediaTypes.add(MediaType.valueOf("text/plain;charset=UTF-8"));
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		converter.setSupportedMediaTypes(supportedMediaTypes);
		converters.add(converter);
	}

}
