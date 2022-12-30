package com.example.library.studentlibrary.services;

import com.example.library.studentlibrary.models.*;
import com.example.library.studentlibrary.repositories.BookRepository;
import com.example.library.studentlibrary.repositories.CardRepository;
import com.example.library.studentlibrary.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    int max_allowed_books;

    @Value("${books.max_allowed_days}")
    int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        //conditions required for successful transaction of issue book:
        //1. book is present and available
        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");
        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");
        //If the transaction is successful, save the transaction to the list of transactions and return the id

        //Note that the error message should match exactly in all cases

        Card card = cardRepository5.findById(cardId).get();
        Book book = bookRepository5.findById(bookId).get();

        if(book == null && book.isAvailable() == false){
            throw new Exception("Book is either unavailable or not present");
        }

        if(card == null || card.getCardStatus().equals(CardStatus.DEACTIVATED)){
            throw new Exception("Card is invalid");
        }

        else if(card.getBooks().size() > max_allowed_books){
            throw new Exception("Book limit has reached for this card");
        }

        else {
            book.setCard(card);
            if(card.getBooks() == null){
                List<Book> books = new ArrayList<>();
                books.add(book);
                card.setBooks(books);
            }
            else {
                card.getBooks().add(book);
            }

            Transaction transaction = Transaction.builder().
                    book(book).card(card).isIssueOperation(true).fineAmount(0).transactionStatus(TransactionStatus.SUCCESSFUL).
                    build();

            book.setAvailable(false);
            bookRepository5.updateBook(book);

            if(book.getTransactions() == null){
                ArrayList<Transaction> transactions = new ArrayList<>();
                transactions.add(transaction);
                book.setTransactions(transactions);
            }
            else {
                book.getTransactions().add(transaction);
            }

            transactionRepository5.save(transaction);
            return transaction.getTransactionId();
            
        }
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId,TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        Date date = transaction.getTransactionDate();
        Date currDate = new Date();

        long delayTime = currDate.getTime() - date.getTime();

        long delayDays = (delayTime / (1000 * 60 * 60 * 24)) % 365;

        int fine = 0;

        if(delayDays > getMax_allowed_days){
            int fineDays = (int)(getMax_allowed_days - delayDays);
            fine = -1 * fineDays * fine_per_day;
        }

        Book book = transaction.getBook();
        book.setAvailable(true);
        bookRepository5.updateBook(book);

        Transaction returnedBook = Transaction.builder().
                book(book).card(transaction.getCard()).
                isIssueOperation(true).fineAmount(fine).
                transactionStatus(TransactionStatus.SUCCESSFUL).build();

        transactionRepository5.save(returnedBook);
        return returnedBook;
    }
}