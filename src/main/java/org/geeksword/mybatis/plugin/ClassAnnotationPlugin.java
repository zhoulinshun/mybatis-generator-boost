package org.geeksword.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @Author: zhoulinshun
 * @Description: 实体类/Mapper接口添加任意注解
 * @Date: Created in 2019-05-24 16:07
 */
public class ClassAnnotationPlugin extends PluginAdapter {
    /**
     * 需要添加的注解
     */
    private String[] recordToAddAnnotations;

    private String[] mapperToAddAnnotations;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        recordToAddAnnotations = Optional.ofNullable(properties.getProperty("recordToAddAnnotations")).map(a -> a.split(",")).orElse(new String[0]);
        mapperToAddAnnotations = Optional.ofNullable(properties.getProperty("mapperToAddAnnotations")).map(a -> a.split(",")).orElse(new String[0]);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {

        for (String toAddAnnotation : recordToAddAnnotations) {
            topLevelClass.addImportedType(toAddAnnotation);
            topLevelClass.addAnnotation(getSimpleName(toAddAnnotation));
        }
        return true;
    }

    private String getSimpleName(String name) {
        int index = name.lastIndexOf(".");
        if (index < 0) {
            return "@" + name;
        }
        String substring = name.substring(index + 1);
        return "@" + substring;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        for (String mapperToAddAnnotation : mapperToAddAnnotations) {
            FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(mapperToAddAnnotation);
            interfaze.addImportedType(fullyQualifiedJavaType);
            interfaze.addAnnotation(getSimpleName(mapperToAddAnnotation));
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

}
