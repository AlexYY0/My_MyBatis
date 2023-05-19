package club.emperorws.mapper;

import club.emperorws.entities.Student;
import club.emperorws.entities.StudentType;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * MyBatis测试Mapper
 *
 * @author: EmperorWS
 * @date: 2023/3/4 11:28
 * @description: TestMapper: MyBatis测试Mapper
 */
public interface TestMapper {

    static final Logger logger = LoggerFactory.getLogger(TestMapper.class);

    default List<Student> selectListDefault(@Param("student") Student student) {
        logger.info("selectListDefault方法执行了！");
        return selectList(student);
    }

    List<Student> selectList(@Param("student") Student student);

    List<Student> selectListNoName(Student student);

    <T> List<T> selectListUseType(Student student);

    //不支持
    List<List<String>> selectListUseList(Student student);

    //无法初始化创建数组
    List<String[]> selectListUseArray(Student student);

    List<Student> selectList(String test);

    StudentType<String>[] selectTypeList(Student student);

    StudentType[] selectNoTypeList(Student student);

    <T> T selectOne(@Param("student") Student student);

    List<Map<String,Object>> selectMapList(@Param("student") Student student);
}
