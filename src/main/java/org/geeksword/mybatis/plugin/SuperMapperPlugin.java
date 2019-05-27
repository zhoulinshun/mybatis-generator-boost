package org.geeksword.mybatis.plugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.Context;

import java.util.*;

/**
 * @Author: zhoulinshun
 * @Description: 构建统一父类接口
 * @Date: Created in 2019-05-21 19:45
 */
public class SuperMapperPlugin extends PluginAdapter {
    //父接口所在包 如果没有会默认取context里的配置
    private String basePackage;
    //父接口命名
    private String baseName = "BaseMapper";
    private Context context;
    private List<Method> superMethods = new ArrayList<>();

    public SuperMapperPlugin() {
        FullyQualifiedJavaType id = new FullyQualifiedJavaType("ID");
        FullyQualifiedJavaType t = new FullyQualifiedJavaType("T");

        Method deleteByPrimaryKey = new Method("deleteByPrimaryKey");
        deleteByPrimaryKey.setReturnType(FullyQualifiedJavaType.getIntInstance());
        deleteByPrimaryKey.addParameter(new Parameter(id, "id"));

        Method insert = new Method("insert");
        insert.setReturnType(FullyQualifiedJavaType.getIntInstance());
        insert.addParameter(new Parameter(t, "record"));

        Method insertSelective = new Method("insertSelective");
        insertSelective.setReturnType(FullyQualifiedJavaType.getIntInstance());
        insertSelective.addParameter(new Parameter(t, "record"));

        Method selectByPrimaryKey = new Method("selectByPrimaryKey");
        selectByPrimaryKey.setReturnType(t);
        selectByPrimaryKey.addParameter(new Parameter(id, "id"));

        Method updateByPrimaryKeySelective = new Method("updateByPrimaryKeySelective");
        updateByPrimaryKeySelective.setReturnType(FullyQualifiedJavaType.getIntInstance());
        updateByPrimaryKeySelective.addParameter(new Parameter(t, "record"));

        Method updateByPrimaryKey = new Method("updateByPrimaryKey");
        updateByPrimaryKey.setReturnType(FullyQualifiedJavaType.getIntInstance());
        updateByPrimaryKey.addParameter(new Parameter(t, "record"));
        superMethods.add(deleteByPrimaryKey);
        superMethods.add(insertSelective);
        superMethods.add(insert);
        superMethods.add(selectByPrimaryKey);
        superMethods.add(updateByPrimaryKeySelective);
        superMethods.add(updateByPrimaryKey);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return !introspectedTable.hasPrimaryKeyColumns();
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return !introspectedTable.hasPrimaryKeyColumns();
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return !introspectedTable.hasPrimaryKeyColumns();
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return !introspectedTable.hasPrimaryKeyColumns();
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientBasicUpdateMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    private TextElement select = new TextElement("select");
    private XmlElement include = new XmlElement("include");
    private String deletePattern = "delete from %s";
    private String wherePrimaryKeyPattern = "where %s = #{%s,jdbcType=%s}";

    {
        include.addAttribute(new Attribute("refid", "Base_Column_List"));

    }


    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.size() == 1) {
            IntrospectedColumn introspectedColumn = primaryKeyColumns.get(0);
            List<Element> elements = element.getElements();
            elements.clear();
            elements.add(select);
            elements.add(include);
            elements.add(wherePrimaryKey(introspectedColumn));
        }
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns.size() == 1) {
            IntrospectedColumn introspectedColumn = primaryKeyColumns.get(0);
            List<Element> elements = element.getElements();
            elements.clear();
            elements.add(new TextElement(String.format(deletePattern, introspectedTable.getTableConfiguration().getTableName())));
            elements.add(wherePrimaryKey(introspectedColumn));
        }
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    private TextElement wherePrimaryKey(IntrospectedColumn introspectedColumn) {
        String actualColumnName = introspectedColumn.getActualColumnName();
        String jdbcTypeName = introspectedColumn.getJdbcTypeName();
        return new TextElement(String.format(wherePrimaryKeyPattern, actualColumnName, "id", jdbcTypeName));
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.context = context;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.properties = properties;
        basePackage = Optional.ofNullable(properties.getProperty("basePackage")).orElse(context.getJavaModelGeneratorConfiguration().getTargetPackage());
        baseName = Optional.ofNullable(properties.getProperty("baseName")).orElse(baseName);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        FullyQualifiedJavaType baseInterfaceJavaType = new FullyQualifiedJavaType(basePackage + "." + baseName);
        baseInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("T"));
        baseInterfaceJavaType.addTypeArgument(new FullyQualifiedJavaType("ID"));
        Interface anInterface = new Interface(baseInterfaceJavaType);
        anInterface.setVisibility(JavaVisibility.PUBLIC);
        superMethods.forEach(anInterface::addMethod);

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(anInterface, context.getJavaModelGeneratorConfiguration().getTargetProject(), new DefaultJavaFormatter());
        return Collections.singletonList(generatedJavaFile);
    }


    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        if (primaryKeyColumns == null || primaryKeyColumns.size() == 0) {
            return true;
        }
        FullyQualifiedJavaType baseMapper = new FullyQualifiedJavaType(basePackage + "." + baseName);
        baseMapper.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        if (primaryKeyColumns.size() == 1) {
            baseMapper.addTypeArgument(introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType());
        } else {
            baseMapper.addTypeArgument(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
        }
        interfaze.addImportedType(baseMapper);
        interfaze.addSuperInterface(baseMapper);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

}
