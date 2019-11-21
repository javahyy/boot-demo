package boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dreamlu.mica.core.utils.StringUtil;
import net.dreamlu.mica.servlet.error.MicaErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * html请求异常处理
 *
 * @author L.cm
 */
@Configuration
public class ViewErrorController extends MicaErrorController {

	public ViewErrorController(ObjectMapper objectMapper,
                               ErrorAttributes errorAttributes,
                               ServerProperties serverProperties) {
		super(objectMapper, errorAttributes, serverProperties.getError());
	}

	@Override
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		String requestedWith = request.getHeader("X-Requested-With");
		if (StringUtil.isNotBlank(requestedWith) && "XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
			return super.errorHtml(request, response);
		}
		HttpStatus status = this.getStatus(request);
		Map<String, Object> model = new HashMap<>(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
		model.put("code", status.value());
		ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
		String errorPath = getErrorPath() + "/error";
		return modelAndView != null ? modelAndView : new ModelAndView(errorPath, model);
	}
}
