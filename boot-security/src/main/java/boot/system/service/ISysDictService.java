package boot.system.service;

import boot.system.model.SysDict;

import java.util.List;

/**
 * 字典 服务类
 */
public interface ISysDictService {
    List<SysDict> selectAll();
}
