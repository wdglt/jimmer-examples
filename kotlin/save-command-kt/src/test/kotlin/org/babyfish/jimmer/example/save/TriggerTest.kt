package org.babyfish.jimmer.example.save

import org.babyfish.jimmer.example.save.common.AbstractMutationWithTriggerTest
import org.babyfish.jimmer.example.save.model.BookStore
import org.babyfish.jimmer.example.save.model.addBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal


/**
 * Recommended learning sequence: 8
 *
 *
 * SaveModeTest -> IncompleteObjectTest -> ManyToOneTest ->
 * OneToManyTest -> ManyToManyTest -> BulkTest -> RecursiveTest -> [Current: TriggerTest]
 */
class TriggerTest : AbstractMutationWithTriggerTest() {
    
    @Test
    fun test() {
        
        jdbc("insert into book_store(id, name) values(?, ?)", 1L, "MANNING")
        jdbc(
            "insert into book(id, name, edition, price, store_id) values" +
                "(?, ?, ?, ?, ?)," +
                "(?, ?, ?, ?, ?)",
            1L, "Microservices Security in Action", 1, BigDecimal("33.59"), 1L,
            2L, "LINQ in Action", 1, BigDecimal("21.59"), 1L
        )
        jdbc(
            ("insert into author(id, first_name, last_name, gender) values" +
                "(?, ?, ?, ?), (?, ?, ?, ?)," +
                "(?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?)"),
            1L, "Prabath", "Siriwardena", "M",
            2L, "Nuwan", "Dias", "M",
            3L, "Fabrice", "Marguerie", "M",
            4L, "Steve", "Eichert", "M",
            5L, "Jim", "Wooley", "M"
        )
        
        jdbc(
            ("insert into book_author_mapping(book_id, author_id) values" +
                "(?, ?), (?, ?), " +
                "(?, ?), (?, ?), (?, ?)"),
            1L, 1L, 1L, 2L,
            2L, 3L, 2L, 4L, 2L, 5L
        )
        
        sql.save(
            BookStore {
                name = "TURING"
                books().addBy {
                    name = "Microservices Security in Action"
                    edition = 1
                    price = BigDecimal("43.59")
                    authors().addBy {
                        id = 1L
                    }
                    authors().addBy {
                        id = 2L
                    }
                    authors().addBy {
                        id = 3L
                    }
                }
                books().addBy {
                    name = "LINQ in Action"
                    edition = 1
                    price = BigDecimal("31.59")
                    authors().addBy {
                        id = 4L
                    }
                    authors().addBy {
                        id = 5L
                    }
                }
            }
        )

        /*
         * This example focuses on triggers, so we don't assert SQL statements,
         * but directly assert events
         */
        
        assertEvents(

            "The entity \"org.babyfish.jimmer.example.save.model.BookStore\" is changed, " +
                "old: null, " +
                "new: {\"id\":100,\"name\":\"TURING\"}",

            "The entity \"org.babyfish.jimmer.example.save.model.Book\" is changed, " +
                "old: {\"id\":1,\"name\":\"Microservices Security in Action\",\"edition\":1,\"price\":33.59,\"store\":{\"id\":1}}, " +
                "new: {\"id\":1,\"name\":\"Microservices Security in Action\",\"edition\":1,\"price\":43.59,\"store\":{\"id\":100}}",

            "The association \"org.babyfish.jimmer.example.save.model.Book.store\" is changed, " +
                "source id: 1, " +
                "detached target id: 1, " +
                "attached target id: 100",

            "The association \"org.babyfish.jimmer.example.save.model.BookStore.books\" is changed, " +
                "source id: 1, " +
                "detached target id: 1, " +
                "attached target id: null",

            "The association \"org.babyfish.jimmer.example.save.model.BookStore.books\" is changed, " +
                "source id: 100, " +
                "detached target id: null, " +
                "attached target id: 1",

            "The entity \"org.babyfish.jimmer.example.save.model.Book\" is changed, " +
                "old: {\"id\":2,\"name\":\"LINQ in Action\",\"edition\":1,\"price\":21.59,\"store\":{\"id\":1}}, " +
                "new: {\"id\":2,\"name\":\"LINQ in Action\",\"edition\":1,\"price\":31.59,\"store\":{\"id\":100}}",

            "The association \"org.babyfish.jimmer.example.save.model.Book.store\" is changed, " +
                "source id: 2, " +
                "detached target id: 1, " +
                "attached target id: 100",

            "The association \"org.babyfish.jimmer.example.save.model.BookStore.books\" is changed, " +
                "source id: 1, " +
                "detached target id: 2, " +
                "attached target id: null",

            "The association \"org.babyfish.jimmer.example.save.model.BookStore.books\" is changed, " +
                "source id: 100, " +
                "detached target id: null, " +
                "attached target id: 2",

            "The association \"org.babyfish.jimmer.example.save.model.Book.authors\" is changed, " +
                "source id: 1, " +
                "detached target id: null, " +
                "attached target id: 3",

            "The association \"org.babyfish.jimmer.example.save.model.Author.books\" is changed, " +
                "source id: 3, " +
                "detached target id: null, " +
                "attached target id: 1",

            "The association \"org.babyfish.jimmer.example.save.model.Book.authors\" is changed, " +
                "source id: 2, " +
                "detached target id: 3, " +
                "attached target id: null",

            "The association \"org.babyfish.jimmer.example.save.model.Author.books\" is changed, " +
                "source id: 3, " +
                "detached target id: 2, " +
                "attached target id: null"
        )
    }
}