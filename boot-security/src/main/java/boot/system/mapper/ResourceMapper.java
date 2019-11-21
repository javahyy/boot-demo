package boot.system.mapper;

import boot.system.model.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资源 Mapper 接口
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    @Select("SELECT r.permissions FROM t_resource r, t_role_resource rr WHERE " +
            "r.id = rr.resource_id AND r.status = 1 AND rr.role_id IN (${roleIds})")
    List<String> findPermissionsByRoleIds(@Param("roleIds") String roleIds);
}
