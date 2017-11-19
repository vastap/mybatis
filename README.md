# MyBatis и Spring - это просто

## <a name="toc"></a> Содержание:
- [Gradle Project Init](#GradleInit)
- [Настройка SpringBoot](#Configuration)
- [Объект предметной области](#DomainObject)
- [Добавляем REST веб-сервис](#WebService)
- [Подключаем DataSource](#AddDataSource)
- [Подключаем MyBatis](#MyBatis)
- [Вставка данных и MyBatis](#MyBatisInsert)
- [Массовое добавление данных и MyBatis](#MyBatisBatch)
- [Работа через аннотации](#AnnotationBased)

## <a name="GradleInit"></a> Gradle Init java-application
Первый шаг - создание проекта.
Современные системы сборки, такие как Gradle или Maven, позволяют автоматизировать этот процесс. Они помогут создать структуру каталогов и базовые файлы, относящиеся непосредственно к самой системе сборки. Это полезно тем, что не надо искать шаблоны для них, т.к. там нужно указать определённые тэги, определённые схемы и т.п.

Мы выбираем систему сборки: Gradle. Так что создавать проект мы будем через него.
Для этого перейдём на сайт Gradle, там перейдём в раздел [Docs](https://gradle.org/docs/). Там перейдём в [User Manual](https://docs.gradle.org/current/userguide/userguide.html).

Нас интересует содержимое **"III. Writing Gradle build scripts"**.
А точнее, **[Chapter 17. Build Init Plugin](https://docs.gradle.org/current/userguide/build_init_plugin.html)**.
Там сказано, что:
```
The Gradle Build Init plugin can be used to bootstrap the process of creating a new Gradle build.
```
То, что нужно!

Открываем командную строку. Например, при работе Если работаем на Windows, то нажимаем ```Win+R```, набираем **cmd** и нажимаем Enter.
Переходим в каталог, где будет наш проект. При необходимости создать его при помощи команды **mkdir**.
Например, перейдём сюда: ```cd C:\_repo\mybatis```.

Определяемся с типом приложения. Нам нужно создать Java приложение.
Следовательно, выполняем: ```gradle init --type java-application``` , как указано в документации, в главе 17, которую мы открыли ранее.

## <a name="Configuration"></a> Настройка
Для учебных целей выберем что-нибудь простое для редактирования. Для этих целей идеально подходит **Sublime Text 3**.
Взять его можно тут: **[официальный сайт Sublime](https://www.sublimetext.com/)**

Запускаем Sublime Text. Выбираем в меню "File" → "Open Folder".
Выбираем тот каталог, в котором мы выполняли команду gradle init.

Для удобства, когда Sublime откроет нам каталог, можно попросить его сохранить открытое как проект: ```"Project" → "Save Project As"```, сохраняем файл в корень каталога нашего проекта.

Теперь мы готовы превратить наш проект - в Spring Boot проект.
Для этого нужно изменить Build Script - файл **build.gradle**.

Чтобы понять, как подключить Spring, перейдём на [официальный сайт Spring](https://spring.io/)'а.
Нам нужно открыть документацию. Выбираем раздел "Projects", а в нём выбираем проект - Spring Boot. На странице находим таблицу с ссылками на документацию. Находим маркер "current" и переходим по ссылке "Reference".
Будет открыт документ: [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/).

Нас интересует раздел [4. Working with Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#_working_with_spring_boot).

Выбираем Build System - [Gradle](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-gradle).
Читаем данный раздел, переносим себе конфигурацию, где описаны plugins.
Пример:
```
plugins {
    id 'org.springframework.boot' version '1.5.8.RELEASE'
    id 'java'
}

repositories {
    jcenter()
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web")
    testCompile("org.springframework.boot:spring-boot-starter-test")
}
```
Видим, что в зависимостях у нас добавлен стартер для Spring web.
Про стартеры подробнее можно прочитать в документации Spring Boot, раздел [13.5 Starters](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter).

Так же мы видим, что помимо плагина для Spring используется плагин - **java**.
Прочитаем документацию для Gradle [Java plugin](https://docs.gradle.org/current/userguide/java_plugin.html) и посмотрим, не нужно ли нам что-нибудь ещё.
И видим, что кое-что полезное есть. Поэтому, скопируем себе следующую конфигурацию:
```
sourceCompatibility = 1.8
targetCompatibility = 1.8
jar {
     baseName = 'mybatis'
     version =  '0.1.0'
}
```
basename - название артефакта, который будет собираться. Поэтому, здесь можно задать любое имя, которое может быть сохранено.

Когда мы читали главу c примером того, как подключать Spring в Gradle проект, мы заметили интересную фразу:
```
The spring-boot-gradle-plugin is also available and provides tasks to create executable jars and run projects from source.
```
То что нужно! Ищем пример, где **spring-boot-gradle-plugin** используется в блоке **buildscript**. Это позволит нам запускать tasks, которые предоставляет плагин. Скопируем себе в самое начало Build Script. Это важно, что в самое начало!
```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:1.5.8.RELEASE")
    }
}
```

До первого запуска осталось совсем чуть-чуть.
Перейдём в документации по Spring Boot в раздел [III. Using Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot).
Нас интересует часть [14. Structuring your code](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-structuring-your-code).
Тут важное замечание:
```
The use of the “default package” is generally discouraged, and should be avoided.
```
Следовательно, нам нужно создать пакет.
Выберем в Sublime Text каталог **src/main/java**. Через контекстное меню выберем "Create folder" создадим каталог для пакета. Например, mybatis.
Далее выберем созданный каталог и через контекстное меню выберем "New File".
Заполним пустой файл текстом:
```java
package mybatis;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```
В Sublime Text в меню выберем ```"File" → "Save"```. Сохраним как Java файл с названием класса. Всё как полагается.

Теперь можем удалить старый класс, который сгенерировал нам Gradle Init в default package. Кроме того, не забудем удалить тест этого класса из **src/test/java**.

Теперь, откроем корневой каталог в командной строке (в Windows можно зажав Shift кликнуть правой кнопкой мыши по пустому месту в каталоге и выбрать в контекстном меню пункт "Открыть окно команд").
Выполняем команду ```gradle task```. В разделе **"Application tasks"** видим полезную нам task - ```bootRun```. Выполняем этот таск: ```gradle bootRun```.

Если всё сделано верно, то мы увидим сообщения вида:
```
Tomcat started on port(s): 8080 (http)
mybatis.App Started App in 12.381 seconds (JVM running for 14.167)
```

## <a name="DomainObject"></a> Объект предметной области
Отлично, наш веб-сервер запускается. Это хорошо. Теперь, нам нужно описать объект из той предметной области, которую мы будем реализовывать.
Например, мы будем реализовывать просмотр и добавление сообщений.
Реализуем простейший класс для этого и назовём его Message:
```java
package mybatis;

public class Message {

    private long id;
    private String content;

    public Message(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Message(String content) {
        this.content = content;
    }

}
```
Данный класс предельно просто. У нас есть 2 поля - ID сообщения и текст сообщения.
Поля приватные. Ну и есть 2 конструктора - один заполняет и id и сообщение. Второй - только сообщение. Всё просто. Осталось только сгенерировать геттеры и сеттеры. Только труд это неблагодарный, даже с учётом автогенерации через IDE.
За нас уже давно всё придумано.
Для этих целей мы можем использовать проект - [Lombok](https://projectlombok.org/).
На официальном сайте переходим в раздел [Install -> Gradle](https://projectlombok.org/setup/gradle). И повторяем тоже у себя в build.gradle, добавив в блок dependencies:
```
compile("org.projectlombok:lombok:1.16.18")
```
Переходим на сайте проекта Lombok в раздел [Features -> Stable](https://projectlombok.org/features/all). Кликаем на "@Getter and @Setter" и узнаём, что это та нужная нам "фича".
Теперь в классе Message мы можем указать импорт:
```java
import lombok.Getter;
import lombok.Setter;
```
И указать, что мы хотим генерировать геттеры и сеттеры, проаннотировав поля:
```java
@Getter @Setter private long id;
@Getter @Setter private String content;
```
В итоге наш класс Message выходит очень компактным:
```java
package mybatis;

import lombok.Getter;
import lombok.Setter;

public class Message {

    @Getter @Setter private long id;
    @Getter @Setter private String content;

    public Message(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Message(String content) {
        this.content = content;
    }

}
```

## <a name="WebService"></a> Добавляем REST Веб-сервис
Теперь, добавим в наш проект Rest Service.
Как мы помним, в наш проект уже добавлен некий стартер: **spring-boot-starter-web**.
Ещё раз вернёмся в раздел документации [13.5 Starters](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-starter). Найдём в списке стартеров наш и прочитаем, что:
```
Starter for building web, including RESTful
```
Получается, поддержка REST веб-сервисов у нас уже есть. Осталось научиться их писать.

В документации Spring Boot можно найти полезный раздел: [27. Developing web applications](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-developing-web-applications). В нём рассказывается про аннотацию @RestController.
Создадим новый класс в ранее созданном нами пакете и скопируем туда пример из документации:
```java
package mybatis;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping(value="/messages")
public class MessageController {

 	@RequestMapping(value="/id/{messageId}", method=RequestMethod.GET)
    public Message getMessage(@PathVariable long messageId) {
        return new Message(messageId, "Stub");
    }
}
```
Теперь, мы можем перезапустить наш сервер при помощи ```gradle bootRun```.
Перейдём на страницу ```http://127.0.0.1:8080/messages/id/1``` и увидим текст:{"id":1,"content":"Stub"}. Ура, работает! )

Кстати, в конце раздела [27.1 The ‘Spring Web MVC framework’](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc) можем увидеть ссылку на [Guides](https://spring.io/guides). Они могут быть полезны. Например, чтобы не листать документацию.
Для REST есть руководство: [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/).
Стоит совмещать чтение документации с примерами из руководства. Тогда будет более целостная картина мира )
Ну, на этом наш веб-сервис готов к работе. Можем продолжать.


## <a name="AddDataSource"></a> Подключаем DataSource
Теперь, нашему веб-сервису нужен какой-то источник данных, откуда он будет возвращать сообщения или наоборот, сохранять. Для этого есть DataSource.
Нашим DataSource, т.е. источником данных, будет SQL база данных.
Так и поищем в документации Spring Boot.
На что найдём раздел: [29. Working with SQL databases](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-sql)

Как мы видим в документации, нам нужен новый стартер для работы с базами данных.
Подключим его в Build скрипте, добавим в зависимости новый стартер:
```
compile("org.springframework.boot:spring-boot-starter-jdbc")
```

Далее, в разделе [29.1.2 Connection to a production database](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-connect-to-production-database) видим информацию о том, как указать, как подключаться к БД. Точнее, как указать спрингу то, каким образом ОН должен подключаться к БД )

Одним из источников настроек в Spring Boot является файл **application.properties**, лежащий в каталоге ресурсов. Подробнее см документацию Spring Boot. Например, раздел [24. Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config).
Поэтому, создадим каталог **src/main/resources**, а в нём файл **application.properties**.

Теперь, необходимо указать настройки для DataSource, как это указано в документации Spring Boot, в разделе [29.1.2 Connection to a production database](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-connect-to-production-database).
Хотелось следовательно инструкции, но увидел размер всего для MySQL... и решил, что PostgreSQL актуальнее ) И весит 60 мегабайт против 300+ мб за MySQL.
Поэтому, переходим на сайт с PostgreSQL и скачиваем: https://www.openscg.com/bigsql/postgresql/installers.jsp
После установки у нас есть 2 варианта:
- На Windows через группу, созданную в пуске
- Из каталога /bin выполнять ```postgres -D ../data```

Там же запускаем pgAdmin. Убедимся, что база работает.
Выбираем в меню "Файл" -> "Добавить сервер".
Хост: 127.0.0.1. Логин и пароль такие, какие мы указали при установке.
Проверив, приступим к конфигурированию.

В документации находим, как указываем [jdbc url](https://jdbc.postgresql.org/documentation/80/connect.html) и [jdbc driver](https://jdbc.postgresql.org/documentation/81/load.html).
Открываем **src/main/resources/application.properties**:
```
# DataBase connection
spring.datasource.url=jdbc:postgresql://127.0.0.1/postgres
spring.datasource.username=postgres
spring.datasource.password=12345
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.continue-on-error=false
```
Теперь, нашему приложению надо откуда-то взять PostgreSQL драйвер. Иначе:
``IllegalStateException: Cannot load driver class: org.postgresql.Driver``
Как и всё в нашем мире систем сборок, нужно попросить Gradle всё сделать.
Для этого, найдём в репозитории [mvnrepository](https://mvnrepository.com/artifact/org.postgresql/postgresql) указание того, как импортировать. И добавим к себе зависимость:
```
compile("org.postgresql:postgresql:42.1.4")
```

Предположим, что это работает. Теперь нужно в БД создавать таблицы, если там было пусто. Для этого нам поможет такая штука, как FlyWay. Об этом мы можем прочитать в главе про инициализацию БД: [78.4 Initialize a Spring Batch database](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-initialize-a-spring-batch-database).
Как и сказано, добавим зависимость в наш Build скрипт:
```
compile("org.flywaydb:flyway-core:4.2.0")
```
Кроме того, в наш application.properties добавим настройку:
```
# FlyWay
# Will migrate from this folder on startup
flyway.baselineOnMigrate=true
```
И добавим простенький скрипт в каталог **src/main/resources/db/migration**, как указано в документации, глава [78.5.1 Execute Flyway database migrations on startup](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html#howto-execute-flyway-database-migrations-on-startup):
```sql
create table MESSAGE (
    ID bigserial,
    TEXT varchar(100) not null
);
```
Про типы данных PostgreSQL смотреть в документации: "[8.1. Numeric Types](https://www.postgresql.org/docs/current/static/datatype-numeric.html)". Важно понимать, какие типы данных используются и как они могут быть соотнесены с типами в Java. Например, SERIAL - это Integer в Java, а BIGSERIAL - это Long. О том, почему это важно, будет видно далее.

Сохраним его с именем: **V1__Create_message_table.sql**
Ориентироваться можно на [Get Started](https://flywaydb.org/getstarted/firststeps/gradle) раздел на сайте FlyWay.
Хотелось бы сделать акцент ещё на том, что SERIAL - это автоинкремент, чтобы ID высчитывался сам. Во многих реализациях БД автоинкремент реализован по-своему. Поэтому, зачастую, при использовании разных реализаций БД существует разный набор инкрементальных скриптов. По той же причине нужны разные драйверы для разных БД.

Теперь хотим, чтобы у нас всегда было одно сообщение в БД.
Поэтому, создадим новый файл: **V2__Add_message.sql**
```sql
insert into MESSAGE (TEXT) values ('First message');
```
Теперь при старте сервера, если ранее не была инициализирована БД, она будет инициализирована данными скриптами. Важно не менять их, иначе сервер будет запускаться с ошибками. Нужно изменение - новый скрипт. Или придётся очищать таблицу **schema_version**, в которой записаны результаты применения всех скриптов и чексуммы.

## <a name="MyBatis"></a> Подключаем MyBatis
Теперь, нам надо как-то работать с Базой данных.
Существует широко распостранённое решение - Hibernate. Это реализация спецификации JPA. Но мы воспользуемся его альтернативой - MyBatis. Он не реализует спецификацию JPA. В отличии от Hibernate маппинг происходит не на таблицы в БД (как в Hibernate), а на SQL запросы, которые разработчик пишет сам.

Переходим на сайт mybatis: http://mybatis.org/
Переходим в верхнем меню в раздел **Products**.
Находим табличку **"Integration"** и переходим на "[Spring Boot Starter docs](http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)".
Соответственно, добавляем в наш Gradle Build Script зависимость.
Версию можно на [mvnrepository.com](https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter).
```
compile("org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.1")
```

Теперь, надо понять, как это использовать. Внизу страницы есть раздел: "Running Samples". То что нужно - это примеры.
Для примера можно воспользоваться вторым примером - конфигурация в виде xml файла.

Сначала, опишем конфигурацию самого MyBatis. Сделаем по аналогии с примером. Найдём в репозитории примера файл **[src/main/resources/mybatis-config.xml](https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-samples/mybatis-spring-boot-sample-xml/src/main/resources/mybatis-config.xml)**.
Чтобы понимать, что из этого что - воспользуемся так же [MyBatis Configuration Documentation](http://www.mybatis.org/mybatis-3/configuration.html).
Напишем свою конфигурацию:
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeAliases>
        <package name="mybatis"/>
    </typeAliases>
    <mappers>
        <mapper resource="MessagesMapper.xml"/>
    </mappers>
</configuration>
```
Данный маппер будет искать мапперы в пакете mybatis. И будет их соотносить с перечисленными мапперами.
**ВАЖНО:** Нежелательно использовать package mybatis, как тут указано. Например, при старте вы получите WARNING о том, что MyBatis не смог проинспектировать файлы. Хотя на пример из данного README это не сказывается. Но от греха подальше лучше пакет переименовать. Например, в learn.epam.

Теперь сконфигурируем сам маппер. Создадим рядом с нашим mybatis-config.xml в каталоге ресурсов новый файл - **MessagesMapper.xml**.
На основе примеров из [src/main/resources/sample/mybatis/mapper](https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-samples/mybatis-spring-boot-sample-xml/src/main/resources/sample/mybatis/mapper) напишем свой маппер:
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.MessageMapper">
    <!-- Select запрос -->
    <select id="findById" parameterType="long" resultType="mybatis.Message">
    SELECT id, text FROM messages WHERE id = #{id}
    </select>

</mapper>
```
Мы указали, что мы передадим тип входящего параметра как long.
**ВАЖНО**, что вернут нам mybatis.Message. Т.е. MyBatis будет пытаться использовать конструктор. Важно, что типы он будет выбирать для параметров основываясь не на типах полей, а на основе тех типов, которые указаны в базе. Например, если тип id в базе указан как serial, то MyBatis будет пытаться использовать integer. И если id в конструкторе указан как long - всё упадёт с ошибкой. Поэтому важно заранее понимать, как будут соотносится типы в БД и в java коде!!

Так же интересно тут то, что мы указали какой-то namespace. На самом деле - этот namespace - это интерфейс, через который мы будем работать.
Поэтому, в пакете mybatis создадим интерфейс MessageMapper:
```java
package mybatis;

import org.apache.ibatis.annotations.Mapper;

/**
 * Интерфейс маппера.
 * <p>Описывает контракт взаимодействия с маппером.
 * За маппером стоит xml, в котором должны быть объявлены такие же названия методов.
 * Если названия будут отличаться:
 * MyBatis не сможет соотнести методы в классе и методы в XML и упадёт.
 */
@Mapper
public interface MessageMapper {
    Message findById(long id);

}
```

Теперь нам нужен DAO класс для работы с БД:
```java
package mybatis;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

@Component
public class MessageDao {
    private final SqlSession sqlSession;

    /**
     * Создание DAO для работы с Message
     *
     * @param sqlSession Sql Session, которая предоставляется Spring Framework
     * @link <a href=http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/>Documentation</a>
     * @link <a href=http://www.mybatis.org/spring/using-api.html>MyBatis API</a>
     */
    public MessageDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

	/**
     * Получить сообщение по его ID
     *
     * @param id Идентификационный номер сообщения
     * @return Сообщение, найденное по его ID
     */
    public Message selectMessageById(long id) {
        MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
        Message message = mapper.findById(id);
        return message;
    }
}
```

Теперь, осталось сделать так, чтобы наш веб-сервис умел работать с нужным DAO и возвращал из базы данные.
Для этого, добавим в наш **MessageController** поле, содержащее в себе DAO:
```java
private MessageDao messageDao;
```
Теперь, добавим конструктор, который будет получать от Spring DI:
```java
@Autowired
public MessageController(MessageDao messageDao) {
	this.messageDao = messageDao;
}
```
Не забываем так же указать импорт:
```java
import org.springframework.beans.factory.annotation.Autowired;
```

Ну и теперь нужно заставить метод получения сообщения выводить результат из дао:
```java
@RequestMapping(value="/{messageId}", method=RequestMethod.GET)
public Message getMessage(@PathVariable long messageId) {
	return messageDao.selectMessageById(messageId);
}
```

Теперь мы можем запускать сервер.
После запуска выполняем: http://127.0.0.1:8080/messages/id/1
Получаем ответ: {"id":1,"content":"First message"}
Ура, работает! ) Вот мы и сделали супер простой проект на MyBatis и Spring Boot )
Но рано расслабляться ) Мы хотим, чтобы мы могли добавлять значения.

## <a name="MyBatisInsert"></a> Вставка данных и MyBatis
Итак, мы теперь можем получить через REST данные. Теперь, надо научиться их добавлять в БД. MyBatis, конечно же, умеет и такое. Иначе бы он был бесполезен

Начнём с самого главного - объявим действие в нашем xml маппере, т.е. в MessagesMapper.xml:
```xml
<!-- Insert запрос -->
<insert id="addMessage" parameterType="mybatis.Message" useGeneratedKeys="true" keyProperty="id" keyColumn="id">insert into MESSAGE (TEXT) values (#{content})
</insert>
```
Мы указываем, что по id = "addMessage" будем принимать параметр mybatis.Message, для которого в БД используется автогенерируемый ключ. ID этого ключа содержится в колонке id, а мы хотим его "смапить" на проперти id (т.е. на поле объекта id).
Ну и указываем запрос, которым нужно вставить содержимое в БД.

Далее, нам нужно добавить новый метод в интерфейс маппера MessageMapper.java:
```java
// Return type of mapped insert method can be void or int (count of inserted rows)
int addMessage(Message message);
```
Мапперы, выполняющие insert, возвращать могут только или ничего или int, как указано в комментарии.

Далее в DAO для данного класса, т.е. в MessageDao добавим так же метод:
```java
public Message addMessage(Message message) {
	MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
	mapper.addMessage(message);
	return message;
}
```

Остался финальный штрих - добавить метод добавления сообщения в контроллер:
```java
@RequestMapping("/add")
public Message addMessage(@RequestParam(value = "text", defaultValue = "empty") String text) {
	Message message = new Message(text);
	return messageDao.addMessage(message);
}
```

Перезапускаем сервер.
Отправляем GET запрос: http://127.0.0.1:8080/messages/add?text=testString
Получаем ответ вида: {"id":7,"content":"testString"}

Как мы видим, выполнился insert. Кроме того, сохраняя объект мы передаём его без ID, а при сохранении MyBatis сам указывает для объекта его id. Вот мы уже умеем добавлять значения. Неплохо! )

## <a name="MyBatisBatch"></a> Массовое добавление данных и MyBatis
Иногда может понадобится добавить несколько значений. В этом случае, возникает проблема с тем, что мы хотим знать id всех добавленных записей. И хотим оптимально использовать соединения.

Начнём как обычно с xml описания маппера. Добавим в MessagesMapper.xml новый маппинг:
```xml
<!-- Mass insertion -->
<insert id="addMessages" keyProperty="id" useGeneratedKeys="true">
	insert into message (text) values
	<foreach collection="list" separator="," item="message">
		(#{message.content})
    </foreach>
</insert>
```

Теперь, обновим интерфейс, на который ссылается наша xml конфигурация, т.е. тот, на который указывает namespace="mybatis.MessageMapper".
Добавим в данный интерфейс новый метод:
```java
int addMessages(List<Message> messageList);
```

Теперь, пойдём в наш DAO (MessageDao) и добавим метод:
```java
public List<Message> addMessages(List<Message> messageList) {
	MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
	mapper.addMessages(messageList);
	return messageList;
}
```
Теперь, добавим обработку GET запроса на добавление. Добавим в MessageController новый метод:
```java
@RequestMapping("/addall")
public List<Message> addMessages(@RequestParam("text") String[] text) {
	List<Message> messages = new ArrayList<>();
	for (String messageText : text) {
		messages.add(new Message(messageText));
	}
	return messageDao.addMessages(messages);
}
```

Теперь мы можем добавить несколько сообщений при помощи GET запроса вида:
http://127.0.0.1:8080/messages/addall?text=one&text=two

Возможность получения ID при массовой вставке появилась только начиная с MyBatis 3.3.1. Подробнее см. https://github.com/mybatis/mybatis-3/pull/547. Так же на работу этого механизма может влияеть реализация БД. Например, с этим проблемы у h2db.

## <a name="AnnotationBased"></a> Работа через аннотации
В последнее время активно развивается уход от xml к аннотациям и java-based конфигурациям. Сервлеты, спринг и т.д. MyBatis не исключение.
Переходим на официальный сайт MyBatis: http://www.mybatis.org/mybatis-3/
В меню слева переходим в раздел [Getting Started](http://www.mybatis.org/mybatis-3/getting-started.html).
По аналогии добавим в наш MessageMapper новый метод:
```java
@Select("SELECT * FROM message WHERE id = #{id}")
Message getById(long id);
```

Добавляем так же метод в DAO:
```java
public Message getMessageById(long id) {
	MessageMapper mapper = sqlSession.getMapper(MessageMapper.class);
	Message message = mapper.getById(id);
	return message;
}
```

Ну и добавим метод в контроллер:
```java
@RequestMapping(value = "/get/{messageId}", method = RequestMethod.GET)
public Message getMessageById(@PathVariable long messageId) {
	return messageDao.getMessageById(messageId);
}
```

Если мы теперь выполним запрос http://127.0.0.1:8080/messages/get/1, то увидим, что всё отрабатывает так же корректно, как и через xml конфигурацию.
