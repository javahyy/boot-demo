package boot.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户角色
 */
@Getter
@Setter
@TableName("t_admin_role")
public class AdminRole implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 用户id
	 */
	@TableField("admin_id")
	private Integer adminId;
	/**
	 * 角色id
	 */
	@TableField("role_id")
	private Integer roleId;

}
