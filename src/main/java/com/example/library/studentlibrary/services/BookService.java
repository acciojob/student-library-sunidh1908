package com.example.library.studentlibrary.services;

import com.example.library.studentlibrary.models.Author;
import com.example.library.studentlibrary.models.Book;
import com.example.library.studentlibrary.models.Genre;
import com.example.library.studentlibrary.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository2;

    public void createBook(Book book){
        Book.builder().id(book.getId()).name(book.getName()).author(book.getAuthor()).genre(book.getGenre()).
                card(book.getCard()).available(book.isAvailable()).transactions(book.getTransactions()).build();
        bookRepository2.save(book);
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = new ArrayList<>();
        if(available == true){
            if (genre != null && author != null){
                books.addAll(bookRepository2.findBooksByGenreAuthor(genre,author,true));
            }
            else if(genre != null && author == null){
                books.addAll(bookRepository2.findBooksByGenre(genre,true));
            }
            else if(genre == null && author != null){
                books.addAll(bookRepository2.findBooksByAuthor(author,true));
            }
            else{
                books.addAll(bookRepository2.findByAvailability(true));
            }
        }
        else {
            if (genre != null && author != null){
                books.addAll(bookRepository2.findBooksByGenreAuthor(genre,author,false));
            }
            else if(genre != null && author == null){
                books.addAll(bookRepository2.findBooksByGenre(genre,false));
            }
            else if(genre == null && author != null){
                books.addAll(bookRepository2.findBooksByAuthor(author,false));
            }
            else{
                books.addAll(bookRepository2.findByAvailability(false));
            }
        }
        return books;
    }
}