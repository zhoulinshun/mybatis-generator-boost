package org.geeksword.mybatis;

import org.geeksword.mybatis.plugin.*;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.PluginAggregator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in 2019-05-27 16:25
 */
public class StandardPlugin extends PluginAdapter {

    private boolean enableClassAnnotation;
    private boolean enableSerializable;
    private boolean enableGeneratedKey;
    private boolean enableComment;
    private boolean enableIndexMethod;
    private boolean enableJavax;
    private boolean enableLombok;
    private boolean enableRemoveBaseMap;
    private boolean enableAddSuperMapper;
    private Context context;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.context = context;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        enableClassAnnotation = Optional.ofNullable(properties.getProperty("enableClassAnnotation")).map(Boolean::new).orElse(false);
        enableSerializable = Optional.ofNullable(properties.getProperty("enableSerializable")).map(Boolean::new).orElse(false);
        enableGeneratedKey = Optional.ofNullable(properties.getProperty("enableGeneratedKey")).map(Boolean::new).orElse(false);
        enableComment = Optional.ofNullable(properties.getProperty("enableComment")).map(Boolean::new).orElse(false);
        enableIndexMethod = Optional.ofNullable(properties.getProperty("enableIndexMethod")).map(Boolean::new).orElse(false);
        enableJavax = Optional.ofNullable(properties.getProperty("enableJavax")).map(Boolean::new).orElse(false);
        enableLombok = Optional.ofNullable(properties.getProperty("enableLombok")).map(Boolean::new).orElse(false);
        enableRemoveBaseMap = Optional.ofNullable(properties.getProperty("enableRemoveBaseMap")).map(Boolean::new).orElse(false);
        enableAddSuperMapper = Optional.ofNullable(properties.getProperty("enableAddSuperMapper")).map(Boolean::new).orElse(false);
        PluginAggregator plugins = (PluginAggregator) context.getPlugins();

        FieldSortPlugin fieldSortPlugin = new FieldSortPlugin();
        fieldSortPlugin.setContext(context);
        fieldSortPlugin.setProperties(properties);
        plugins.addPlugin(fieldSortPlugin);

        if (enableClassAnnotation) {
            ClassAnnotationPlugin classAnnotationPlugin = new ClassAnnotationPlugin();
            classAnnotationPlugin.setContext(context);
            classAnnotationPlugin.setProperties(properties);
            plugins.addPlugin(classAnnotationPlugin);
        }

        if (enableSerializable) {
            SerializablePlugin serializablePlugin = new SerializablePlugin();
            serializablePlugin.setContext(context);
            serializablePlugin.setProperties(properties);
            plugins.addPlugin(serializablePlugin);
        }
        if (enableGeneratedKey) {
            GeneratedKeyPlugin generatedKeyPlugin = new GeneratedKeyPlugin();
            generatedKeyPlugin.setContext(context);
            generatedKeyPlugin.setProperties(properties);
            plugins.addPlugin(generatedKeyPlugin);
        }
        if (enableComment) {
            ICommentGenerator iCommentGenerator = new ICommentGenerator();
            iCommentGenerator.addConfigurationProperties(properties);
            set(context, "commentGenerator", iCommentGenerator);
        }
        if (enableIndexMethod) {
            IndexMethodPlugin indexMethodPlugin = new IndexMethodPlugin();
            indexMethodPlugin.setContext(context);
            indexMethodPlugin.setProperties(properties);
            plugins.addPlugin(indexMethodPlugin);
        }
        if (enableJavax) {
            JavaxAnnotationPlugin javaxAnnotationPlugin = new JavaxAnnotationPlugin();
            javaxAnnotationPlugin.setContext(context);
            javaxAnnotationPlugin.setProperties(properties);
            plugins.addPlugin(javaxAnnotationPlugin);
        }
        if (enableLombok) {
            LombokAnnotationPlugin lombokAnnotationPlugin = new LombokAnnotationPlugin();
            lombokAnnotationPlugin.setContext(context);
            lombokAnnotationPlugin.setProperties(properties);
            plugins.addPlugin(lombokAnnotationPlugin);
        }
        if (enableRemoveBaseMap) {
            RemoveBaseMapPlugin removeBaseMapPlugin = new RemoveBaseMapPlugin();
            removeBaseMapPlugin.setContext(context);
            removeBaseMapPlugin.setProperties(properties);
            plugins.addPlugin(removeBaseMapPlugin);
        }
        if (enableAddSuperMapper) {
            SuperMapperPlugin superMapperPlugin = new SuperMapperPlugin();
            superMapperPlugin.setContext(context);
            superMapperPlugin.setProperties(properties);
            plugins.addPlugin(superMapperPlugin);
        }

    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    private void set(Object object, String field, Object value) {
        try {
            Field declaredField = object.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T> T get(Object object, String field) {
        try {
            Field declaredField = object.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            return (T) declaredField.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
