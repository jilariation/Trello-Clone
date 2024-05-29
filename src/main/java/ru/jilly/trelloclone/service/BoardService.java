package ru.jilly.trelloclone.service;

import ru.jilly.trelloclone.dto.board.AddBoardRequest;
import ru.jilly.trelloclone.dto.board.BoardRequest;
import ru.jilly.trelloclone.dto.board.BoardResponse;
import ru.jilly.trelloclone.entity.Board;
import ru.jilly.trelloclone.entity.User;

import java.util.List;
import java.util.UUID;

public interface BoardService {
    List<BoardResponse> getAllBoards();
    BoardResponse getBoardById(UUID id);
    BoardResponse addBoard(AddBoardRequest boardDto, User user);
    BoardResponse updateBoardById(UUID id, BoardRequest boardDto, User user);
    BoardResponse addMemberToBoardById(UUID id, UUID memberId, User user);
    void removeMemberFromBoardById(UUID id, UUID memberId, User user);
    void deleteBoardById(UUID id, User user);
    void validatePermissions(Board board, User currentUser);
    void validateOwnership(Board board, User currentUser);
    List<BoardResponse> getMemberedBoardsByUserId(UUID id);
    List<BoardResponse> getCreatedBoardsByUserId(UUID id);
}
