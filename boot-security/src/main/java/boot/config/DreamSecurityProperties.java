package boot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * mica 安全框架配置
 */
@Component
@ConfigurationProperties("mica.security")
public class DreamSecurityProperties {

	/**
	 * 忽略的地址
	 */
	@Getter
	private List<String> permitAll = new ArrayList<>();

	@Getter
	private final Login login = new Login();

	/**
	 * 登录配置
	 */
	@Getter
	@Setter
	public static class Login {
		/**
		 * 登录重试锁定次数，默认：5
		 */
		private int retryLimit = 5;
		/**
		 * 登录重试锁定cache名，默认：retryLimitCache
		 */
		private String retryLimitCacheName = "retryLimitCache";
	}
}
