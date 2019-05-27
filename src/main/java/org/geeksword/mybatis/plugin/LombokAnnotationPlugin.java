package org.geeksword.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @Author: zhoulinshun
 * @Description: 添加lombok注解
 * @Date: Created in 2019-01-03 14:49
 */
public class LombokAnnotationPlugin extends PluginAdapter {
    private final String data = "@Data";
    private final String builder = "@Builder";
    private final String allArgs = "@AllArgsConstructor";
    private final String noArgs = "@NoArgsConstructor";
    private final FullyQualifiedJavaType allArgsJavaType;
    private final FullyQualifiedJavaType noArgsJavaType;
    private final FullyQualifiedJavaType dataJavaType;
    private final FullyQualifiedJavaType builderJavaType;

    private boolean addData;

    private boolean addBuilder;

    private boolean addAllArgsConstructor;

    private boolean addNoArgsConstructor;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        addData = Optional.ofNullable(properties.getProperty("addData")).map(Boolean::new).orElse(true);
        addBuilder = Optional.ofNullable(properties.getProperty("addBuilder")).map(Boolean::new).orElse(false);
        addAllArgsConstructor = Optional.ofNullable(properties.getProperty("addAllArgsConstructor")).map(Boolean::new).orElse(false);
        addNoArgsConstructor = Optional.ofNullable(properties.getProperty("addNoArgsConstructor")).map(Boolean::new).orElse(false);
    }

    public LombokAnnotationPlugin() {
        dataJavaType = new FullyQualifiedJavaType("lombok.Data");
        builderJavaType = new FullyQualifiedJavaType("lombok.Builder");
        allArgsJavaType = new FullyQualifiedJavaType("lombok.AllArgsConstructor");
        noArgsJavaType = new FullyQualifiedJavaType("lombok.NoArgsConstructor");
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {

        addLombokAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addLombokAnnotation(topLevelClass, introspectedTable);
        return true;
    }

    private void addLombokAnnotation(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {
        if (addAllArgsConstructor) {
            topLevelClass.addImportedType(allArgsJavaType);
            topLevelClass.addAnnotation(allArgs);
        }
        if (addBuilder) {
            topLevelClass.addImportedType(builderJavaType);
            topLevelClass.addAnnotation(builder);
        }
        if (addNoArgsConstructor) {
            topLevelClass.addImportedType(noArgsJavaType);
            topLevelClass.addAnnotation(noArgs);
        }
        if (addData) {
            topLevelClass.addImportedType(dataJavaType);
            topLevelClass.addAnnotation(data);
        }
    }

    /**
     * 该方法在生成每一个属性的getter方法时候调用，如果我们不想生成getter，直接返回false即可；
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }


    /**
     * 该方法在生成每一个属性的setter方法时候调用，如果我们不想生成setter，直接返回false即可；
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

}
