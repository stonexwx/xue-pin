package cn.org.qsmx.service.impl;

import cn.org.qsmx.enums.Sex;
import cn.org.qsmx.enums.ShowWhichName;
import cn.org.qsmx.enums.UserRole;
import cn.org.qsmx.exceptions.GraceException;
import cn.org.qsmx.fegin.ResumeMicroServiceFeign;
import cn.org.qsmx.mapper.UsersMapper;
import cn.org.qsmx.mq.InitResumeMQConfig;
import cn.org.qsmx.pojo.Users;
import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import cn.org.qsmx.service.UsersService;
import cn.org.qsmx.util.DesensitizationUtil;
import cn.org.qsmx.util.LocalDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xwx
 * @since 2022-12-14
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    private final String IMG_URL = "http://img.qsmx.org.cn/i/2022/12/20/63a14a746328a.png";
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private ResumeMicroServiceFeign resumeMicroServiceFeign;
    /**
     * 判断用户是否存在，如果存在则返回用户信息，否则null
     *
     * @param mobile
     * @return
     */
    @Override
    public Users queryMobileExist(String mobile) {

        return usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("mobile",mobile));
    }

    /**
     * 创建用户信息，并且返回用户对象
     *
     * @param mobile
     * @return
     */
    @Transactional
    @Override
    public Users createUsers(String mobile) {

        Users users = new Users();
        users.setMobile(mobile);
        users.setNickname("用户"+ DesensitizationUtil.commonDisplay(mobile));
        users.setRealName("用户"+ DesensitizationUtil.commonDisplay(mobile));
        users.setShowWhichName(ShowWhichName.nickname.type);
        users.setSex(Sex.secret.type);
        users.setFace(IMG_URL);
        LocalDate birthDate = LocalDateUtils
                .parseLocalDate("1980-01-01",LocalDateUtils.DATE_PATTERN);
        users.setBirthday(birthDate);

        users.setCountry("中国");
        users.setProvince("");
        users.setCity("");
        users.setDescription("");
        users.setDescription("这家户很懒，啥都没有留下来~");

        users.setStartWorkDate(LocalDate.now());
        users.setPosition("");
        users.setRole(UserRole.CANDIDATE.type);
        users.setHrInWhichCompanyId("");

        users.setCreatedTime(LocalDateTime.now());
        users.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(users);

        //发起远程调用，初始化用户简历
//        resumeMicroServiceFeign.init(users.getId());

        return users;
    }

    @Autowired
    public RabbitTemplate rabbitTemplate;

    /**
     * 使用mq完成最终一致性
     * @param mobile
     * @return
     */
    @Transactional
    @Override
    public Users createUsersAndInitResumeMQ(String mobile) {

        //创建用户
        Users users = createUsers(mobile);



        //发送消息，初始化简历
//        rabbitTemplate.convertAndSend(InitResumeMQConfig.INIT_RESUME_EXCHANGE,
//                InitResumeMQConfig.ROUTING_KEY_INIT_RESUME,
//                users.getId());
        return users;
    }
}
