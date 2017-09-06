package com.yao.storehomeui.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/1.
 * 类工具
 */
public class ClassUtil {
    /**
     * 判断指定对象是否是基础类型
     *
     * @param clz 对象
     */
    public static boolean isWrapClass(Class clz) {
        try {
            if (clz.isPrimitive())
                return true;
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 反射获取对象属性
     *
     * @param cls
     */
    protected final static Field[] getFiled(Class<?> cls) {
        List<Field> fds = new ArrayList<Field>();
        for (; cls != Object.class; cls = cls.getSuperclass()) {
            for (Field f : cls.getDeclaredFields()) {
                if (f != null) {
                    fds.add(f);
                }
            }
        }
        return fds.toArray(new Field[fds.size()]);
    }

    protected final static Field getField(Object o, String fname) {
        Field field = null;
        Class<?> cls = o.getClass();
        for (; cls != Object.class; cls = cls.getSuperclass()) {
            try {
                field = cls.getDeclaredField(fname);
                return field;
            } catch (Exception e) {
                // TODO 不做任何异常获取，如果属性为获取会自动报错
            }
        }
        return null;
    }

    public final static void setPrivateParameter(Object o, String fname, Object fParameter) {
        Field field = ClassUtil.getField(o, fname);
        field.setAccessible(true);
        try {
            field.set(o, fParameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public final static <T> T getPrivateParameter(Object o, String fname) {
        Field field = ClassUtil.getField(o, fname);
        field.setAccessible(true);
        try {
            return (T) field.get(o);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
