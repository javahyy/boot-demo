package boot.system.service;

import boot.common.result.Tree;
import boot.system.model.Role;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * 角色 服务类
 */
public interface IRoleService extends IService<Role> {

    List<Tree> selectTree();

    /**
     * 根据用户id查找角色
     *
     * @param adminId 用户id
     * @return 角色集合
     */
    List<Role> findListByAdminId(Integer adminId);

    List<Integer> selectResourceIdListByRoleId(Integer id);

    void updateRoleResource(Integer id, String resourceIds);
}
