package org.babyfish.jimmer.sql.example.model;

import org.babyfish.jimmer.sql.*;
import org.babyfish.jimmer.sql.example.model.common.BaseEntity;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Entity
public interface BookStore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key // (1)
    String name();

    @Nullable // (2)
    String website();

    /**
     * All books belonging to the current bookstore,
     * representing a one-to-many association
     */
    @OneToMany(mappedBy = "store", orderedProps = { // (3)
            @OrderedProp("name"),
            @OrderedProp(value = "edition", desc = true)
    })
    List<Book> books();

    // -----------------------------
    //
    // Everything below this line are calculated properties.
    //
    // The complex calculated properties are shown here.
    // As for the simple calculated properties, you can view `Author.fullName`
    // -----------------------------

    /**
     * This is a calculated scalar property,
     * the average price of all books in the current bookstore
     */
    @Transient(ref = "bookStoreAvgPriceResolver") // (4)
    BigDecimal avgPrice();

    /*
     * For example, if `BookStore.books` returns `[
     *     {name: A, edition: 1}, {name: A, edition: 2}, {name: A, edition: 3},
     *     {name: B, edition: 1}, {name: B, edition: 2}
     * ]`, `BookStore.newestBooks` returns `[
     *     {name: A, edition: 3}, {name: B, edition: 2}
     * ]`
     *
     * It is worth noting that if the calculated property returns entity object
     * or entity list, the shape can be controlled by the deeper child fetcher
     */

    /**
     * This is a calculated associated property.
     *
     * <p>Since books have an {@link Book#edition()}
     * properties, the books relationship may contain
     * books with the same name.</p>
     *
     * This collection only selects books with the highest
     * edition to avoid naming conflicts,
     * therefore this collection is always a subset of the
     * {@link #books()} collection
     */
    @Transient(ref = "bookStoreNewestBooksResolver") // (5)
    List<Book> newestBooks();
}

/*----------------Documentation Links----------------
(1) https://babyfish-ct.github.io/jimmer-doc/docs/mapping/advanced/key
(2) https://babyfish-ct.github.io/jimmer-doc/docs/mapping/base/nullity
(3) https://babyfish-ct.github.io/jimmer-doc/docs/mapping/base/association/one-to-many
(4) (5) https://babyfish-ct.github.io/jimmer-doc/docs/mapping/advanced/calculated/transient
---------------------------------------------------*/
