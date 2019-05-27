package org.geeksword.mybatis.plugin;

import lombok.Getter;
import org.mybatis.generator.api.ConnectionFactory;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.internal.ObjectFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: zhoulinshun
 * @Description: 根据索引生成查询方法
 * @Date: Created in 2019-05-23 11:43
 */
public class IndexMethodPlugin extends PluginAdapter {

    private Map<String, List<Method>> addMethod = new HashMap<>();

    private Map<String, List<IndexInfo>> indexInfoMap = new HashMap<>();

    @Getter
    private static class IndexInfo {
        //表类别
        public String TABLE_CAT;
        //表模式
        public String TABLE_SCHEM;
        //表名
        public String TABLE_NAME;

        //索引值是否可以不惟一
        public boolean NON_UNIQUE;

        //索引类别
        public String INDEX_QUALIFIER;
        public String INDEX_NAME;
        //索引类型
        //tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息
        //tableIndexClustered - 此为集群索引
        //tableIndexHashed - 此为散列索引
        //tableIndexOther - 此为某种其他样式的索引
        public String TYPE;
        // 索引中的列序列号；TYPE 为 tableIndexStatistic 时该序列号为零
        public Integer ORDINAL_POSITION;
        //列名
        public String COLUMN_NAME;
        // 列排序序列，”A” => 升序，”D” => 降序，如果排序序列不受支持，可能为 null；TYPE 为 tableIndexStatistic 时排序序列为 null
        public String ASC_OR_DESC;
        //TYPE 为 tableIndexStatistic 时，它是表中的行数；否则，它是索引中惟一值的数量。
        public Integer CARDINALITY;
        //TYPE 为 tableIndexStatisic 时，它是用于表的页数，否则它是用于当前索引的页数
        public Integer PAGES;
        //过滤器条件，如果有的话。（可能为 null）
        public String FILTER_CONDITION;

        public IndexInfo(ResultSet resultSet) {
            Field[] fields = this.getClass().getFields();
            for (Field field : fields) {
                try {
                    Object object = resultSet.getObject(field.getName(), field.getType());
                    field.set(this, object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        ConnectionFactory connectionFactory;
        JDBCConnectionConfiguration jdbcConnectionConfiguration = context.getJdbcConnectionConfiguration();
        if (jdbcConnectionConfiguration != null) {
            connectionFactory = new JDBCConnectionFactory(jdbcConnectionConfiguration);
        } else {
            connectionFactory = ObjectFactory.createConnectionFactory(context);
        }
        try (Connection connection = connectionFactory.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            List<TableConfiguration> tableConfigurations = context.getTableConfigurations();

            //get all index
            for (TableConfiguration tableConfiguration : tableConfigurations) {
                List<IndexInfo> indexInfos = new ArrayList<>();
                ResultSet indexInfo = metaData.getIndexInfo(null, connection.getSchema(), tableConfiguration.getTableName(), false, true);
                while (indexInfo.next()) {
                    indexInfos.add(new IndexInfo(indexInfo));
                }
                indexInfoMap.put(tableConfiguration.getTableName(), indexInfos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    private final String paramAnnotationPattern = "@Param(\"%s\")";
    private final FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
    private final FullyQualifiedJavaType listType = new FullyQualifiedJavaType("java.util.List");

    /**
     * 接口中生成对应的查询方法
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        String configurationType = introspectedTable.getContext().getJavaClientGeneratorConfiguration().getConfigurationType();
        boolean userAnnotation = Objects.equals("ANNOTATEDMAPPER", configurationType) || Objects.equals("MIXEDMAPPER", configurationType);

        List<IndexInfo> indexInfos = indexInfoMap.get(tableName);
        Map<String, List<IndexInfo>> indexMapper = indexInfos.stream()
                //去掉主键索引
                .filter(indexInfo -> !indexInfo.getINDEX_NAME().equals("PRIMARY"))
                .collect(Collectors.toMap(IndexInfo::getINDEX_NAME, Collections::singletonList, (l1, l2) -> {
                    ArrayList<IndexInfo> arrayList = new ArrayList<>(l1);
                    arrayList.addAll(l2);
                    return arrayList;
                }));

        Map<String, IntrospectedColumn> fieldMapper = introspectedTable.getBaseColumns().
                stream().collect(Collectors.toMap(IntrospectedColumn::getActualColumnName, i -> i));

        //等待添加的方法
        List<Method> toAddMethod = new ArrayList<>(indexMapper.values().size());

        //java.util.List
        FullyQualifiedJavaType collectJavaType = new FullyQualifiedJavaType("java.util.List");
        //RecordType
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        //type argument
        collectJavaType.addTypeArgument(recordType);
        indexMapper.forEach((indexName, value) -> {
            //add param import
            interfaze.addImportedType(paramType);
            //add return value import
            interfaze.addImportedType(listType);
            StringBuilder selectBy = new StringBuilder("selectBy");
            Method method = new Method();
            for (int i = 0; i < value.size(); i++) {
                IndexInfo indexInfo = value.get(i);
                IntrospectedColumn introspectedColumn = fieldMapper.get(indexInfo.COLUMN_NAME);
                //field name
                String javaProperty = introspectedColumn.getJavaProperty();
                selectBy.append(javaProperty.substring(0, 1).toUpperCase()).append(javaProperty.substring(1));
                if (i + 1 != value.size()) {
                    selectBy.append("And");
                }
                //field type
                FullyQualifiedJavaType columnJavaType = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(i, new Parameter(columnJavaType, javaProperty, String.format(paramAnnotationPattern, javaProperty)));
            }
            if (value.get(0).NON_UNIQUE) {
                method.setReturnType(collectJavaType);
            } else {
                method.setReturnType(recordType);
            }
            method.setName(selectBy.toString());

            if (userAnnotation) {
                String select = "@Select(\"" + "select " + makeSelectColumn(introspectedTable) + "\n" +
                        "from " + tableName + "\n" + makeWhere(method, introspectedTable);
                method.addAnnotation(select);
            }
            toAddMethod.add(method);
        });

        addMethod.put(tableName, toAddMethod);
        toAddMethod.forEach(interfaze::addMethod);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    /**
     * xml生成对应的查询方法
     *
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement rootElement = document.getRootElement();
        String configurationType = introspectedTable.getContext().getJavaClientGeneratorConfiguration().getConfigurationType();
        boolean userAnnotation = Objects.equals("ANNOTATEDMAPPER", configurationType) || Objects.equals("MIXEDMAPPER", configurationType);
        if (userAnnotation) return true;

        List<Method> methods = addMethod.get(introspectedTable.getTableConfiguration().getTableName());
        for (Method method : methods) {
            rootElement.addElement(makeSelectElement(method, introspectedTable));
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private TextElement select = new TextElement("select");
    private XmlElement include = new XmlElement("include");

    {
        include.addAttribute(new Attribute("refid", "Base_Column_List"));
    }

    private Element makeSelectElement(Method method, IntrospectedTable introspectedTable) {
        List<Parameter> parameters = method.getParameters();
        Map<String, IntrospectedColumn> fieldMapper = introspectedTable.getAllColumns().stream().collect(Collectors.toMap(IntrospectedColumn::getJavaProperty, l -> l));


        XmlElement selectRoot = new XmlElement("select");
        selectRoot.addAttribute(new Attribute("id", method.getName()));
        selectRoot.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        List<Element> elements = selectRoot.getElements();
        //select
        elements.add(select);
        //c1,c2,c3
        elements.add(include);
        //from table
        elements.add(new TextElement("from " + introspectedTable.getTableConfiguration().getTableName()));
        String where = makeWhere(method, introspectedTable);
        elements.add(new TextElement(where));
        return selectRoot;
    }

    private String makeSelectColumn(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();
        StringBuilder columns = new StringBuilder();
        for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
            columns.append(primaryKeyColumn.getActualColumnName()).append(", ");
        }
        for (IntrospectedColumn baseColumn : baseColumns) {
            columns.append(baseColumn.getActualColumnName()).append(" ,");
        }
        String sql = columns.toString();
        return sql.substring(0, sql.length() - 2);
    }

    private String makeWhere(Method method, IntrospectedTable introspectedTable) {
        List<Parameter> parameters = method.getParameters();
        Map<String, IntrospectedColumn> fieldMapper = introspectedTable.getAllColumns().stream().collect(Collectors.toMap(IntrospectedColumn::getJavaProperty, l -> l));
        StringBuilder where = new StringBuilder("where ");
        for (int i = 0; i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);
            IntrospectedColumn introspectedColumn = fieldMapper.get(parameter.getName());
            String conditionPattern = "%s = #{%s, jdbcType=%s}";
            where.append(String.format(conditionPattern, introspectedColumn.getActualColumnName(), getParamName(parameter), introspectedColumn.getJdbcTypeName()));
            if (i + 1 != parameters.size()) {
                where.append(" and ");
            }
        }
        return where.toString();
    }


    private Pattern pattern = Pattern.compile("@Param\\(\"([a-z]+)\"\\)");


    private String getParamName(Parameter parameter) {
        List<String> annotations = parameter.getAnnotations();
        for (String annotation : annotations) {
            Matcher matcher = pattern.matcher(annotation);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return parameter.getName();
    }
}
