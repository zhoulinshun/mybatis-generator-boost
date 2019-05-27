##mybatis 逆向工程增强

###0、org.geeksword.mybatis.StandardPlugin
```text
下列插件的集合，可以只引入一个插件来达到引入下列全部插件的效果
```

###1、 org.geeksword.mybatis.ICommentGenerator
####描述
```text
将数据库表的注释添加到对应的字段和类上
```
####可选属性值
* suppressAllComments boolean 默认false 隐藏所有注释
* datePattern String 日期的格式
 
 
###2、 org.geeksword.mybatis.JavaxAnnotationPlugin
####描述
```text
添加 javax注解 
对于非空字段，添加javax.validation.constraints.NotNull注解标示
对于主键ID，添加javax.persistence.Id 注释标识
对于有精度要求的字段，添加 javax.validation.constraints.Digits
对于有长度要求的字段，添加 javax.validation.constraints.Size
```
####可选属性值
* enableNotNull boolean  默认false 添加@NotNull 
* enableId boolean 默认false 添加@Id
* enableDigits boolean 默认false 添加@Digits           
* enableSize boolean 默认false 添加@Size

###3、 org.geeksword.mybatis.LombokAnnotationPlugin
####描述
```text
添加lombok注解，避免生成大量的getter/setter
```
####可选属性值
* addData boolean 默认true 
* addBuilder boolean 默认false   
* addAllArgsConstructor boolean 默认false
* addNoArgsConstructor boolean 默认false


###4、 org.geeksword.mybatis.SerializablePlugin
####描述
```text
对mybatis原生提供的序列化插件增强，对于生成的Example类和相应的内部类也添加上序列化接口
```
####可选属性值
* addGWTInterface boolean 默认false 是否添加GWT序列化接口
* suppressJavaInterface boolean 默认true 是否不添加jdk的序列化接口


###5、 org.geeksword.mybatis.GeneratedKeyPlugin
####描述
```text
对于自增主键，在insert/inertSelective的mapper文件中添加主键id的返回
```
>> ps:mysql中，对于自增主键，如果是InnoDB引擎的表，只能是单一主键，无法设置联合主键，所以此处只对InnoDB的自增主键有效

####6、 org.geeksword.mybatis.SuperMapperPlugin
####描述
```text
 为所有的Mapper文件添加抽象接口，把根据主键的查询、更新、删除方法放入到父类接口中
 每一个批次生产的mapper文件类的主键方法最好保持一致，否则会出现一些奇怪的问题
 默认抽象接口名为 BaseMapper
 如果表没有主键，则不会生成抽象接口
```
####可选属性值
* basePackage 抽象接口所在的包路径
* baseName 抽象接口的命名

###7、 org.geeksword.mybatis.RemoveBaseMapPlugin
####描述
```text
移除自动生产了BaseResult，为查询方法加入 resultType
可以配合下列配置一起使用
mybatis.configuration.map-underscore-to-camel-case=true
```

###8、org.geeksword.mybatis.IndexMethodPlugin
####描述
```text
根据主键生成对应的查询方法
默认会不会使用ResultMap，要在配置enableResultMap来开启
```
####可选属性值
* useResultMap boolean 默认true 返回值是否使用ResultMap

###9、org.geeksword.mybatis.ClassAnnotationPlugin
####描述
```text
给生成的model/mapper类添加任意注解
```
####可选属性
* recordToAddAnnotations 添加到model上的注解，多个注解用逗号分割
* mapperToAddAnnotations 添加到Mapper上的注解，多个注解用逗号分割