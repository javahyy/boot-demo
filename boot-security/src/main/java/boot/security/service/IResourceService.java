package boot.security.service;

import boot.common.result.Tree;
import boot.security.auth.AuthUser;
import boot.system.model.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 资源 服务类
 */
public interface IResourceService extends IService<Resource> {

	List<Resource> findByType(Integer type);

	List<Tree> findAllMenu();

	List<String> findPermissionsByRoleIds(List<Integer> roleIds);

	List<Tree> findAllTree();

	List<Tree> findUserTree(AuthUser authUser);

	List<Resource> findAllByAdminId(Integer adminId);
}
