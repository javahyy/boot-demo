package boot.security.service;

import boot.system.vo.AdminVO;
import boot.system.model.Admin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author L.cm
 * @since 2018-01-29
 */
public interface IAdminService extends IService<Admin> {

	/**
	 * 根据用户名查找用户
	 * @param username 用户名
	 * @return 用户
	 */
	Admin findByName(String username);

	IPage<AdminVO> finalDataGrid(AdminVO adminVO, IPage<Admin> pages);

	boolean insertByVo(AdminVO adminVO);

	boolean updateByVo(AdminVO adminVO);
}
