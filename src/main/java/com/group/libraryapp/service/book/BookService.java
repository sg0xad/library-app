package com.group.libraryapp.service.book;


import com.group.libraryapp.domain.book.Book;
import com.group.libraryapp.domain.book.BookRepository;
import com.group.libraryapp.domain.user.User;
import com.group.libraryapp.domain.user.UserRepository;
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository;
import com.group.libraryapp.dto.book.request.BookCreateRequest;
import com.group.libraryapp.dto.book.request.BookLoanRequest;
import com.group.libraryapp.dto.book.request.BookReturnRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BookService {
  private final BookRepository bookRepository;
  private final UserLoanHistoryRepository userLoanHistoryRepository;
  private final UserRepository userRepository;

  public BookService(BookRepository bookRepository, UserLoanHistoryRepository userLoanHistoryRepository, UserRepository userRepository) {
    this.bookRepository = bookRepository;
    this.userLoanHistoryRepository = userLoanHistoryRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void saveBook(BookCreateRequest request) {
    bookRepository.save(new Book(request.getName()));
  }

  @Transactional
  public void loanBook(BookLoanRequest request) {
    // 1. 책 정보를 가져온다.
    Book book = bookRepository.findByName(request.getBookName())
            .orElseThrow(IllegalArgumentException::new);

    // 2. 대출기록 정보를 확인해서 대출 중이라면 예외를 발생
    if (userLoanHistoryRepository.existsByBookNameAndIsReturn(book.getName(), false)) {
      throw new IllegalArgumentException("진작 대출되어 있는 책입니다");
    }

    // 3. 유저 정보를 가져옴
    User user = userRepository.findByName(request.getUserName())
            .orElseThrow(IllegalArgumentException::new);

    // 4. 유저 정보와 책 정보를 기반으로 UserLoanHistory를 저장
    // userLoanHistoryRepository.save(new UserLoanHistory(user, book.getName()));
    user.loanBook(book.getName());
  }

  @Transactional
  public void returnBook(BookReturnRequest request) {
    User user = userRepository.findByName(request.getUserName())
            .orElseThrow(IllegalArgumentException::new);
//        UserLoanHistory history = userLoanHistoryRepository.findByUserIdAndBookName(user.getId(), request.getBookName())
//                .orElseThrow(IllegalAccessError::new);
//        history.doReturn();
//        userLoanHistoryRepository.save(history); // 영속성 컨텍스트로 안써도 됨
    user.returnBook(request.getBookName());
  }

}

