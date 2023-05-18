package club.emperorws.mapper;

import club.emperorws.entities.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * MyBatis测试Mapper
 *
 * @author: EmperorWS
 * @date: 2023/3/4 11:28
 * @description: TestMapper: MyBatis测试Mapper
 */
public interface Test1Mapper {

    List<Student> selectList(@Param("student") Student student);

    <T> T selectOne(@Param("student") Student student);

    List<Map<String,Object>> selectMapList(@Param("student") Student student);
}
