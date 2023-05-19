package club.emperorws;

import club.emperorws.entities.RichType;
import club.emperorws.entities.Student;
import club.emperorws.entities.StudentType;
import club.emperorws.mapper.TestMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * todo ${description}
 *
 * @author: ${USER}
 * @date: ${DATE} ${TIME}
 * @description: ${NAME}: ${description}
 */
public class BzMain {

    private static final Logger logger = LoggerFactory.getLogger(BzMain.class);

    public static void main(String[] args) {
        try {
            InputStream input = Resources.getResourceAsStream("MyBatisConfig.xml");
            //默认为DefaultSqlSessionFactory
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
            //默认为DefaultSqlSession
            SqlSession sqlSession = sessionFactory.openSession();
            //获取代理的mapper
            TestMapper mapper = sqlSession.getMapper(TestMapper.class);
            //List<Student> studentList = mapper.selectList(new Student("al","c"));
            //List<Student> studentList = mapper.selectListDefault(new Student("al","c"));
            //List<Student> studentList = mapper.selectListUseType(new Student("al","c"));
            /*List<List<String>> studentStrList = mapper.selectListUseList(new Student("al","c"));
            for (List<String> studentStr : studentStrList) {
                logger.info("studentList is :{}", studentStr);
            }*/
            /*List<String[]> studentStrList = mapper.selectListUseArray(new Student("al","c"));
            for (String[] studentStr : studentStrList) {
                logger.info("studentList is :{}", studentStr);
            }*/
            //StudentType<String>[] studentTypeList = mapper.selectTypeList(new Student("al","c"));
            StudentType[] studentTypeList = mapper.selectNoTypeList(new Student("al","c"));
            for (StudentType studentType : studentTypeList) {
                logger.info("studentList is :{}", studentType);
            }
            //test
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Class<?> setterType = meta.getSetterType("richType.richList[0]");
            Class<?> getterType = meta.getGetterType("richType.richList[0].richField");
            System.out.println(setterType);
            System.out.println(getterType);
            MetaClass arr = MetaClass.forClass(String[].class, reflectorFactory);
            System.out.println("arr.hasGetter(\"[0]\")=" + arr.hasGetter("[0]"));

            RichType<String> richType = new RichType<>();
            MetaObject richTypeMetaObject = sessionFactory.getConfiguration().newMetaObject(richType);
            richTypeMetaObject.setValue("richList[0].richField","new");
            richTypeMetaObject.setValue("richArray[0].richField","new");
            RichType originalRichType = (RichType)richTypeMetaObject.getOriginalObject();
            /*Student student = mapper.selectOne(new Student());
            logger.info("selectOne student is :{}", student);*/
            /*List<Map<String, Object>> mapList = mapper.selectMapList(new Student());
            for (Map<String, Object> map : mapList) {
                logger.info("studentMapList is :{}", map.toString());
            }*/
            /*TypeReference<List<URI>> type = new TypeReference<List<URI>>() {
            };
            Type rawType = type.getRawType();*/
        } catch (IOException e) {
            logger.error("异常", e);
        }
    }
}

class TestType <K extends Comparable & Serializable, V> {
    K key;
    V value;
    public static void main(String[] args) throws Exception {
        // 获取字段的类型
        Field fk = TestType.class.getDeclaredField("key");
        Field fv = TestType.class.getDeclaredField("value");
        // getGenericType
        TypeVariable keyType = (TypeVariable)fk.getGenericType();
        TypeVariable valueType = (TypeVariable)fv.getGenericType();
        // getName 方法
        System.out.println(keyType.getName());
        System.out.println(valueType.getName());
        // getGenericDeclaration 方法
        System.out.println(keyType.getGenericDeclaration());
        System.out.println(valueType.getGenericDeclaration());
        // getBounds 方法
        System.out.println("K 的上界:");
        for (Type type : keyType.getBounds()) {
            System.out.println(type);
        }
        System.out.println("V 的上界:");
        for (Type type : valueType.getBounds()) {
            System.out.println(type);
        }
        TypeVariable<?>[] parentTypeVars = TestType.class.getTypeParameters();
        System.out.println(Arrays.toString(parentTypeVars));
    }
}
