package cn.org.qsmx.mapper;


import cn.org.qsmx.pojo.ar.AdminAR;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 学聘网运营管理系统的admin账户表，仅登录，不提供注册 Mapper 接口
 * </p>
 *
 * @author xwx
 * @since 2023-02-26
 */
@Mapper
public interface AdminMapperAR extends BaseMapper<AdminAR> {

}
