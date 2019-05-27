package org.geeksword.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.*;

/**
 * @Author: zhoulinshun
 * @Description: 添加javax注解
 * @Date: Created in 2019-05-15 12:56
 */
public class JavaxAnnotationPlugin extends PluginAdapter {

    private boolean enableNotNull;

    private boolean enableId;

    private boolean enableDigits;

    private boolean enableSize;

    private FullyQualifiedJavaType id = new FullyQualifiedJavaType("javax.persistence.Id");

    private FullyQualifiedJavaType notNull = new FullyQualifiedJavaType("javax.validation.constraints.NotNull");

    private FullyQualifiedJavaType digest = new FullyQualifiedJavaType("javax.validation.constraints.Digits");

    private final FullyQualifiedJavaType size = new FullyQualifiedJavaType("javax.validation.constraints.Size");

    private static final String digestPattern = "@Digits(integer = %s, fraction = %s)";

    private static final String sizePattern = "@Size(max = %s)";

    private Set<FullyQualifiedJavaType> notAdd = new HashSet<>(Arrays.asList(FullyQualifiedJavaType.getIntInstance(),
            FullyQualifiedJavaType.getDateInstance(),
            FullyQualifiedJavaType.getStringInstance(),
            new FullyQualifiedJavaType("java.lang.Integer"),
            new FullyQualifiedJavaType("java.lang.Short"),
            new FullyQualifiedJavaType("java.lang.Long")));


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (enableId) {
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
            if (primaryKeyColumns.stream().anyMatch(c -> c.equals(introspectedColumn))) {
                topLevelClass.addImportedType(id);
                field.addAnnotation("@Id");
            }
        }

        if (enableNotNull) {
            if (!introspectedColumn.isNullable()) {
                topLevelClass.addImportedType(notNull);
                field.addAnnotation("@NotNull");
            }
        }

        FullyQualifiedJavaType type = field.getType();
        int length = introspectedColumn.getLength();
        int scale = introspectedColumn.getScale();
        if (enableDigits) {
            if (!notAdd.contains(type)) {
                topLevelClass.addImportedType(digest);
                field.addAnnotation(String.format(digestPattern, length, scale));
            }
        }

        if (enableSize) {
            if (type.equals(FullyQualifiedJavaType.getStringInstance())) {
                topLevelClass.addImportedType(size);
                field.addAnnotation(String.format(sizePattern, length));
            }
        }
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        enableNotNull = Optional.ofNullable(properties.getProperty("enableNotNull")).map(Boolean::parseBoolean).orElse(false);
        enableId = Optional.ofNullable(properties.getProperty("enableId")).map(Boolean::parseBoolean).orElse(false);
        enableDigits = Optional.ofNullable(properties.getProperty("enableDigits")).map(Boolean::parseBoolean).orElse(false);
        enableSize = Optional.ofNullable(properties.getProperty("enableSize")).map(Boolean::parseBoolean).orElse(false);

    }
}
