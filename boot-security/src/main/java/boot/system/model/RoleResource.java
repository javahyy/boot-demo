package boot.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 角色资源
 */
@Getter
@Setter
@TableName("t_role_resource")
public class RoleResource implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 角色id
	 */
	@TableField("role_id")
	private Integer roleId;
	/**
	 * 资源id
	 */
	@TableField("resource_id")
	private Integer resourceId;

}
