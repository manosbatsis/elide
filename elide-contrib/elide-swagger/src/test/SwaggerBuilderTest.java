/*
 * Copyright 2016, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package test;

import com.google.common.collect.Maps;
import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.contrib.swagger.model.Info;
import com.yahoo.elide.contrib.swagger.model.Swagger;
import com.yahoo.elide.contrib.swagger.SwaggerBuilder;
import com.yahoo.elide.core.EntityDictionary;
import junit.framework.Assert;
import org.testng.annotations.Test;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Set;

public class SwaggerBuilderTest {
    @Entity
    @Include
    public class Author {
        @OneToMany
        public Set<Book> getBooks() {
            return null;
        }

        @OneToMany
        public Set<Publisher> getPublisher() {
            return null;
        }
    }

    @Entity
    @Include(rootLevel = true)
    public class Publisher {

        @OneToMany
        public Set<Book> getBooks() {
            return null;
        }

        @OneToMany
        public Set<Author> getExclusiveAuthors() {
            return null;
        }
    }

    @Entity
    @Include(rootLevel = true)
    public class Book {
        @OneToMany
        public Set<Author> getAuthors() {
            return null;
        }

        @OneToOne
        public Publisher getPublisher() {
            return null;
        }
    }


    @Test
    public void testPathGeneration() throws Exception {
        EntityDictionary dictionary = new EntityDictionary(Maps.newHashMap());

        dictionary.bindEntity(Book.class);
        dictionary.bindEntity(Author.class);
        dictionary.bindEntity(Publisher.class);

        Info info = new Info();
        info.title = "Test Service";
        info.version = "1.0";

        SwaggerBuilder builder = new SwaggerBuilder(dictionary, info);
        Swagger swagger = builder.build();

        Assert.assertTrue(swagger.paths.containsKey("/publisher"));
        Assert.assertTrue(swagger.paths.containsKey("/publisher/{publisherId}"));
        Assert.assertTrue(swagger.paths.containsKey("/publisher/{publisherId}/exclusiveAuthors"));
        Assert.assertTrue(swagger.paths.containsKey("/publisher/{publisherId}/exclusiveAuthors/{authorId}"));
        Assert.assertTrue(swagger.paths.containsKey("/publisher/{publisherId}/relationships/exclusiveAuthors"));
        Assert.assertTrue(swagger.paths.containsKey("/book"));
        Assert.assertTrue(swagger.paths.containsKey("/book/{bookId}"));
        Assert.assertTrue(swagger.paths.containsKey("/book/{bookId}/authors"));
        Assert.assertTrue(swagger.paths.containsKey("/book/{bookId}/authors/{authorId}"));
        Assert.assertTrue(swagger.paths.containsKey("/book/{bookId}/relationships/authors"));
        Assert.assertEquals(swagger.paths.size(), 10);

        System.err.println(swagger.toString());
        System.err.flush();
    }
}