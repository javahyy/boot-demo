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
 * 组织机构
 */
@Getter
@Setter
@TableName("t_organization")
public class Organization implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 组织名
	 */
	private String name;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 编号
	 */
	private String code;
	/**
	 * 图标
	 */
	@TableField("icon_cls")
	private String iconCls;
	/**
	 * 父级主键
	 */
	private Integer pid;
	/**
	 * 排序
	 */
	private Integer seq;
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
