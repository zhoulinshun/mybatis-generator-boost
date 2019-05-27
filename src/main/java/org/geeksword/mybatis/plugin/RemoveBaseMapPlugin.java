package org.geeksword.mybatis.plugin;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: zhoulinshun
 * @Description: 移除自动生成的ResultMap，
 * @Date: Created in 2019-05-15 14:53
 */
public class RemoveBaseMapPlugin extends PluginAdapter {


    private Attribute resultMap = new Attribute("resultMap", "BaseResultMap");

    private Pattern resultPattern = Pattern.compile("\\s*@Result");

    private Pattern otherPattern = Pattern.compile("\\s*}\\)");


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientBasicSelectOneMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientBasicSelectOneMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectAllMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectByExampleWithBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectByExampleWithBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectByExampleWithoutBLOBsMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectByExampleWithoutBLOBsMethodGenerated(method, topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        removeResultAnnotation(method);
        return super.clientSelectByPrimaryKeyMethodGenerated(method, topLevelClass, introspectedTable);
    }

    private void removeResultAnnotation(Method method) {
        List<String> annotations = method.getAnnotations();
        annotations.removeIf(s -> resultPattern.matcher(s).find());
        Iterator<String> iterator = annotations.iterator();
        while (iterator.hasNext()) {
            if (otherPattern.matcher(iterator.next()).find()) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            Field documentField = sqlMap.getClass().getDeclaredField("document");
            documentField.setAccessible(true);
            Document document = (Document) documentField.get(sqlMap);
            XmlElement rootElement = document.getRootElement();
            Iterator<Element> iterator = rootElement.getElements().iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                if (element instanceof XmlElement) {
                    String name = ((XmlElement) element).getName();
                    if (name.equals("resultMap")) {
                        iterator.remove();
                    } else if (name.equals("select")) {
                        ((XmlElement) element).getAttributes().removeIf(next -> next.compareTo(resultMap) == 0);
                        ((XmlElement) element).addAttribute(new Attribute("resultType", introspectedTable.getBaseRecordType()));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

}
