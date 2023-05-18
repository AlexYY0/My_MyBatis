/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * @author Iwao AVE!
 */
public class TypeParameterResolver {

  /**
   * Resolve field type.
   *
   * @param field
   *          the field
   * @param srcType
   *          the src type
   *
   * @return The field type as {@link Type}. If it has type parameters in the declaration,<br>
   *         they will be resolved to the actual runtime {@link Type}s.
   */
  public static Type resolveFieldType(Field field, Type srcType) {
    Type fieldType = field.getGenericType();
    Class<?> declaringClass = field.getDeclaringClass();
    return resolveType(fieldType, srcType, declaringClass);
  }

  /**
   * Resolve return type.
   *
   * @param method
   *          the method
   * @param srcType
   *          the src type
   *
   * @return The return type of the method as {@link Type}. If it has type parameters in the declaration,<br>
   *         they will be resolved to the actual runtime {@link Type}s.
   */
  public static Type resolveReturnType(Method method, Type srcType) {
    Type returnType = method.getGenericReturnType();
    Class<?> declaringClass = method.getDeclaringClass();
    return resolveType(returnType, srcType, declaringClass);
  }

  /**
   * Resolve param types.
   *
   * @param method
   *          the method
   * @param srcType
   *          the src type
   *
   * @return The parameter types of the method as an array of {@link Type}s. If they have type parameters in the
   *         declaration,<br>
   *         they will be resolved to the actual runtime {@link Type}s.
   */
  public static Type[] resolveParamTypes(Method method, Type srcType) {
    Type[] paramTypes = method.getGenericParameterTypes();
    Class<?> declaringClass = method.getDeclaringClass();
    Type[] result = new Type[paramTypes.length];
    for (int i = 0; i < paramTypes.length; i++) {
      result[i] = resolveType(paramTypes[i], srcType, declaringClass);
    }
    return result;
  }

  /**
   * 获取返回值类型
   *
   * @param type 返回值类型
   * @param srcType 用于获取泛型变量实际类型的类
   * @param declaringClass 执行方法对应的class
   * @return 获取返回值类型
   */
  private static Type resolveType(Type type, Type srcType, Class<?> declaringClass) {
    //TypeVariable是类型变量（泛型变量）
    if (type instanceof TypeVariable) {
      return resolveTypeVar((TypeVariable<?>) type, srcType, declaringClass);
    }
    //具体的泛型类型
    if (type instanceof ParameterizedType) {
      return resolveParameterizedType((ParameterizedType) type, srcType, declaringClass);
    } else if (type instanceof GenericArrayType) {
      //泛型类的数组：List[],Map[]
      return resolveGenericArrayType((GenericArrayType) type, srcType, declaringClass);
    } else {
      return type;
    }
  }

  private static Type resolveGenericArrayType(GenericArrayType genericArrayType, Type srcType,
      Class<?> declaringClass) {
    //获取泛型类型数组的声明类型，即获取数组方括号 [] 前面的部分
    //获取数组泛型中元素的泛型类型，比如: T[] testArray中T的类型
    Type componentType = genericArrayType.getGenericComponentType();
    Type resolvedComponentType = null;
    //继续递归的获取数组的泛型类型
    if (componentType instanceof TypeVariable) {
      // 解析泛型变量:Class<A extends B>
      resolvedComponentType = resolveTypeVar((TypeVariable<?>) componentType, srcType, declaringClass);
    } else if (componentType instanceof GenericArrayType) {
      // 递归解析泛型数组:List<N>[] listArray
      resolvedComponentType = resolveGenericArrayType((GenericArrayType) componentType, srcType, declaringClass);
    } else if (componentType instanceof ParameterizedType) {
      // 解析参数化泛型:List<T> lists
      resolvedComponentType = resolveParameterizedType((ParameterizedType) componentType, srcType, declaringClass);
    }
    // 将解析出的泛型类型转换为数组类型
    if (resolvedComponentType instanceof Class) {
      return Array.newInstance((Class<?>) resolvedComponentType, 0).getClass();
    }
    // 解析出来泛型的实际类型还是一个泛型定义
    return new GenericArrayTypeImpl(resolvedComponentType);
  }

  private static ParameterizedType resolveParameterizedType(ParameterizedType parameterizedType, Type srcType,
      Class<?> declaringClass) {
    //获取原始类型
    Class<?> rawType = (Class<?>) parameterizedType.getRawType();
    //获取泛型类型
    Type[] typeArgs = parameterizedType.getActualTypeArguments();
    //递归调用，找到泛型信息，并组装至实体信息存储
    Type[] args = new Type[typeArgs.length];
    for (int i = 0; i < typeArgs.length; i++) {
      if (typeArgs[i] instanceof TypeVariable) {
        args[i] = resolveTypeVar((TypeVariable<?>) typeArgs[i], srcType, declaringClass);
      } else if (typeArgs[i] instanceof ParameterizedType) {
        args[i] = resolveParameterizedType((ParameterizedType) typeArgs[i], srcType, declaringClass);
      } else if (typeArgs[i] instanceof WildcardType) {
        //通配符类型，即带有?的泛型参数, 例如 List<?>中的？，List<? extends Number>里的? extends Number 和List<? super Integer>的? super Integer
        args[i] = resolveWildcardType((WildcardType) typeArgs[i], srcType, declaringClass);
      } else {
        args[i] = typeArgs[i];
      }
    }
    return new ParameterizedTypeImpl(rawType, null, args);
  }

  private static Type resolveWildcardType(WildcardType wildcardType, Type srcType, Class<?> declaringClass) {
    Type[] lowerBounds = resolveWildcardTypeBounds(wildcardType.getLowerBounds(), srcType, declaringClass);
    Type[] upperBounds = resolveWildcardTypeBounds(wildcardType.getUpperBounds(), srcType, declaringClass);
    return new WildcardTypeImpl(lowerBounds, upperBounds);
  }

  /**
   * @param bounds         类型边界
   * @param srcType        泛型变量所属类
   * @param declaringClass 泛型变量声明类
   */
  private static Type[] resolveWildcardTypeBounds(Type[] bounds, Type srcType, Class<?> declaringClass) {
    Type[] result = new Type[bounds.length];
    // 依次处理泛型边界中的每个具体的泛型定义
    for (int i = 0; i < bounds.length; i++) {
      if (bounds[i] instanceof TypeVariable) {
        // 处理泛型变量
        result[i] = resolveTypeVar((TypeVariable<?>) bounds[i], srcType, declaringClass);
      } else if (bounds[i] instanceof ParameterizedType) {
        // 处理参数化泛型
        result[i] = resolveParameterizedType((ParameterizedType) bounds[i], srcType, declaringClass);
      } else if (bounds[i] instanceof WildcardType) {
        result[i] = resolveWildcardType((WildcardType) bounds[i], srcType, declaringClass);
      } else {
        // 普通Class
        result[i] = bounds[i];
      }
    }
    return result;
  }

  /**
   * 解析指定泛型变量的类型
   *
   * @param typeVar        泛型变量
   * @param srcType        用于获取泛型变量实际类型的类
   * @param declaringClass 实际声明该类型的类的类型
   */
  private static Type resolveTypeVar(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass) {
    Type result;
    Class<?> clazz;
    // step1: 处理srcType，移除泛型定义，获取对应的Class类型
    if (srcType instanceof Class) {
      clazz = (Class<?>) srcType;
    } else if (srcType instanceof ParameterizedType) {
      // 泛型参数取声明泛型的类型
      ParameterizedType parameterizedType = (ParameterizedType) srcType;
      clazz = (Class<?>) parameterizedType.getRawType();
    } else {
      throw new IllegalArgumentException(
          "The 2nd arg must be Class or ParameterizedType, but was: " + srcType.getClass());
    }

    // step2: 获取泛型定义的实际类型
    // case1: 当前类就是声明了泛型的类
    if (clazz == declaringClass) {
      // 当前类就是声明了泛型的类，则泛型一定未被指定具体类型，获取泛型变量类型上限
      Type[] bounds = typeVar.getBounds();
      if (bounds.length > 0) {
        return bounds[0];
      }
      // 没有指定泛型变量上限，那就是Object
      return Object.class;
    }

    // case2: 尝试从父类中获取泛型变量的实际类型
    // 运行期间泛型所属的对象和声明泛型定义的不是同一个
    // 获取其直接父类类型，尝试从父类中找到泛型定义
    Type superclass = clazz.getGenericSuperclass();
    // 递归处理父类，直到找到该泛型对应的实际类型，或者null值。
    result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superclass);
    if (result != null) {
      return result;
    }

    // case3: 无法通过父类获取泛型变量的实际类型,则通过接口定义获取泛型变量对应的实际类型
    // 获取类实现的所有接口，尝试从接口中获取泛型变量的定义
    Type[] superInterfaces = clazz.getGenericInterfaces();
    for (Type superInterface : superInterfaces) {
      result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superInterface);
      if (result != null) {
        return result;
      }
    }
    // 在接口中也未找到泛型变量的实际类型，返回Object类
    return Object.class;
  }

  /**
   * @param typeVar        泛型变量的类型
   * @param srcType        泛型变量所属对象的类
   * @param declaringClass 声明泛型变量的类
   * @param clazz          泛型变量所属对象处理泛型后的类型
   * @param superclass     srcType直接父类或者接口
   */
  private static Type scanSuperTypes(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass, Class<?> clazz,
      Type superclass) {
    // 针对父类的类型定义有三种处理方案
    // 1.参数化泛型
    // 2.普通类同时是声明了泛型的类的实现
    // 3.普通类但不是声明了泛型实现的类的实现
    if (superclass instanceof ParameterizedType) {
      // case1：参数化泛型定义类，比如: Super<A,B>{},Sub extend Super<String,Integer>.即获取：String,Integer
      ParameterizedType parentAsType = (ParameterizedType) superclass;
      // 获取父类除去泛型定义之后的类型，比如Super<A,B>{}的Super.class
      Class<?> parentAsClass = (Class<?>) parentAsType.getRawType();
      // 获取父类中的泛型定义，比如Super<A,B>{}的A，B
      TypeVariable<?>[] parentTypeVars = parentAsClass.getTypeParameters();
      // 泛型变量所属对象的类也是一个泛型对象
      if (srcType instanceof ParameterizedType) {
        // 合并子类和父类的泛型定义比如: Super<A,B>{},Sub extend Super<String,Integer>.
        parentAsType = translateParentTypeVars((ParameterizedType) srcType, clazz, parentAsType);
      }
      // 父类就是声明了泛型变量的类
      if (declaringClass == parentAsClass) {
        for (int i = 0; i < parentTypeVars.length; i++) {
          // 找到了typeVar对应的实际类型定义，A，B对的上号
          if (typeVar.equals(parentTypeVars[i])) {
            // 返回泛型的实际类型
            return parentAsType.getActualTypeArguments()[i];
          }
        }
      }
      // 父类是声明了泛型类的子类，继续递归查找
      if (declaringClass.isAssignableFrom(parentAsClass)) {
        return resolveTypeVar(typeVar, parentAsType, declaringClass);
      }
    } else if (superclass instanceof Class && declaringClass.isAssignableFrom((Class<?>) superclass)) {
      // case2: 父类是一个普通的类，同时父类是泛型变量所属定义类的子实现
      // 递归
      return resolveTypeVar(typeVar, superclass, declaringClass);
    }
    // 父类不是泛型类，同时父类不是泛型变量声明类的实现
    return null;
  }

  private static ParameterizedType translateParentTypeVars(ParameterizedType srcType, Class<?> srcClass,
      ParameterizedType parentType) {
    // 获取父类定义的泛型变量的实际类型数组，比如: Super<A,B>{},Sub extend Super<String,Integer>.即是：A，B
    Type[] parentTypeArgs = parentType.getActualTypeArguments();
    // 获取子类定义的泛型变量的实际类型数组，比如: Super<A,B>{},Sub extend Super<String,Integer>.即是：String,Integer
    Type[] srcTypeArgs = srcType.getActualTypeArguments();
    // 获取子类中的泛型变量定义，比如Super<A,B>{}的A，B
    TypeVariable<?>[] srcTypeVars = srcClass.getTypeParameters();
    Type[] newParentArgs = new Type[parentTypeArgs.length];
    boolean noChange = true;
    for (int i = 0; i < parentTypeArgs.length; i++) {
      if (parentTypeArgs[i] instanceof TypeVariable) {
        for (int j = 0; j < srcTypeVars.length; j++) {
          // 子类泛型定义和父类泛型定义一致,则子类中泛型变量的实参对应着父类泛型变量的实参
          if (srcTypeVars[j].equals(parentTypeArgs[i])) {
            noChange = false;
            // 从子类中取出泛型变量的实际类型
            newParentArgs[i] = srcTypeArgs[j];
          }
        }
      } else {
        // 父类中指定了泛型对应的实际类型
        newParentArgs[i] = parentTypeArgs[i];
      }
    }
    // 合并子类和父类的泛型定义
    return noChange ? parentType : new ParameterizedTypeImpl((Class<?>) parentType.getRawType(), null, newParentArgs);
  }

  private TypeParameterResolver() {
  }

  static class ParameterizedTypeImpl implements ParameterizedType {
    private final Class<?> rawType;

    private final Type ownerType;

    private final Type[] actualTypeArguments;

    public ParameterizedTypeImpl(Class<?> rawType, Type ownerType, Type[] actualTypeArguments) {
      this.rawType = rawType;
      this.ownerType = ownerType;
      this.actualTypeArguments = actualTypeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
      return actualTypeArguments;
    }

    @Override
    public Type getOwnerType() {
      return ownerType;
    }

    @Override
    public Type getRawType() {
      return rawType;
    }

    @Override
    public String toString() {
      return "ParameterizedTypeImpl [rawType=" + rawType + ", ownerType=" + ownerType + ", actualTypeArguments="
          + Arrays.toString(actualTypeArguments) + "]";
    }
  }

  static class WildcardTypeImpl implements WildcardType {
    private final Type[] lowerBounds;

    private final Type[] upperBounds;

    WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
      this.lowerBounds = lowerBounds;
      this.upperBounds = upperBounds;
    }

    @Override
    public Type[] getLowerBounds() {
      return lowerBounds;
    }

    @Override
    public Type[] getUpperBounds() {
      return upperBounds;
    }
  }

  static class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    GenericArrayTypeImpl(Type genericComponentType) {
      this.genericComponentType = genericComponentType;
    }

    @Override
    public Type getGenericComponentType() {
      return genericComponentType;
    }
  }
}
