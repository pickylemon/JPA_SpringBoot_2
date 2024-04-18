package jpabook.jpashop.controller;

import jpabook.jpashop.domain.items.Book;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookForm {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    private String author;
    private String isbn;

    public Book toBook(){
        Book book = new Book();
        book.setName(this.name);
        book.setPrice(this.price);
        book.setStockQuantity(this.stockQuantity);
        book.setIsbn(this.isbn);
        book.setAuthor(this.author);

        return book;
    }

    public static BookForm toBookForm(Book book){
        BookForm form = new BookForm();
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());
        form.setPrice(book.getPrice());
        form.setId(book.getId());
        form.setName(book.getName());
        form.setStockQuantity(book.getStockQuantity());

        return form;
    }
}
