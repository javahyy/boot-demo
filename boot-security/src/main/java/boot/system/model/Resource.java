package boot.system.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.mica.core.utils.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源
 */
@Getter
@Setter
@TableName("t_resource")
public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 资源名称
	 */
	private String name;
	/**
	 * 资源的权限
	 */
	private String permissions;
	/**
	 * 资源路径
	 */
	private String url;
	/**
	 * 打开方式 ajax,iframe
	 */
	@TableField("open_mode")
	private String openMode;
	/**
	 * 资源介绍
	 */
	private String description;
	/**
	 * 资源图标
	 */
	@TableField("icon_cls")
	private String iconCls;
	/**
	 * 父级资源id
	 */
	private Integer pid;
	/**
	 * 排序
	 */
	private Integer seq;
	/**
	 * 打开状态
	 */
	private Boolean opened;
	/**
	 * 资源类别
	 */
	@TableField("resource_type")
	private Integer resourceType;
	/**
	 * 状态[0:失效,1:正常]
	 */
	@TableLogic
	private Integer status;
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	private LocalDateTime createTime;
	/**
	 * 更新时间
	 */
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	private LocalDateTime updateTime;
}
