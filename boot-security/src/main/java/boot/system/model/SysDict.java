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
 * 字典
 */
@Getter
@Setter
@TableName("t_sys_dict")
public class SysDict implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 编码类别
	 */
	@TableField("dict_type")
	private String dictType;
	/**
	 * 编码类别描述
	 */
	@TableField("dict_desc")
	private String dictDesc;
	/**
	 * 字典键
	 */
	@TableField("dict_key")
	private String dictKey;
	/**
	 * 字典值
	 */
	@TableField("dict_value")
	private String dictValue;
	/**
	 * 排序
	 */
	private Integer seq;
	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	@DateTimeFormat(pattern = DateUtil.PATTERN_DATETIME)
	@JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
	private LocalDateTime createTime;
}
