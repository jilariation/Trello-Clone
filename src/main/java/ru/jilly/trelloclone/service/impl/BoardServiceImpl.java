package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.dto.board.AddBoardRequest;
import ru.jilly.trelloclone.dto.board.BoardRequest;
import ru.jilly.trelloclone.dto.board.BoardResponse;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.repo.BoardRepo;
import ru.jilly.trelloclone.service.BoardService;
import ru.jilly.trelloclone.utils.mapper.BoardMapper;
import ru.jilly.trelloclone.entity.Board;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.service.UserService;
import ru.jilly.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.jilly.trelloclone.utils.exception.ResourceMismatchException;
import ru.jilly.trelloclone.utils.exception.ResourceNotFoundException;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepo boardRepo;
    private final UserService userService;
    private final BoardMapper boardMapper;
    private final UserMapper userMapper;

    @Override
    public List<BoardResponse> getAllBoards() {
        return boardRepo.findAll().stream()
                .map(boardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BoardResponse getBoardById(UUID id) {
        return boardMapper.toDto(getExistingBoard(id));
    }

    @Override
    @Transactional
    public BoardResponse addBoard(AddBoardRequest boardDto, User user) {
        Board board = boardMapper.toEntity(boardDto);

        enrichBoard(board);

        board.setCreator(user);
        board.setMembers(createMembersFromDto(boardDto));

        Board savedBoard = boardRepo.save(board);
        log.info("Board with id: {} created", savedBoard.getId());
        return boardMapper.toDto(savedBoard);
    }

    @Override
    @Transactional
    public BoardResponse updateBoardById(UUID id, BoardRequest boardDto, User user) {
        Board board = getExistingBoard(id);

        validateOwnership(board, user);

        boardMapper.mergeDtoIntoEntity(boardDto, board);
        Board savedBoard = boardRepo.save(board);
        log.info("Board created with id: {}", savedBoard.getId());
        return boardMapper.toDto(savedBoard);
    }

    @Override
    @Transactional
    public BoardResponse addMemberToBoardById(UUID id, UUID memberId, User user) {
        Board board = getExistingBoard(id);

        validatePermissions(board, user);

        UserResponse memberDto = userService.getUserById(memberId);
        User member = userMapper.toEntity(memberDto);

        Set<User> membersToUpdate = new HashSet<>(board.getMembers());
        membersToUpdate.add(member);

        board.setMembers(membersToUpdate);
        Board savedBoard = boardRepo.save(board);
        log.info("Board with id: {} updated by user with id: {}", savedBoard.getId(), user.getId());
        return boardMapper.toDto(board);
    }

    @Override
    @Transactional
    public void removeMemberFromBoardById(UUID id, UUID memberId, User user) {
        Board board = getExistingBoard(id);

        validateOwnership(board, user);

        Set<User> membersToUpdate = new HashSet<>(board.getMembers());
        boolean isRemoved = membersToUpdate.removeIf(m -> m.getId().equals(memberId));

        if(isRemoved) {
            board.setMembers(membersToUpdate);
            Board savedBoard = boardRepo.save(board);
            log.info("Member with id: {} removed from board with id: {}", memberId, savedBoard.getId());
        } else {
            throw new ResourceMismatchException("Member with id: " + memberId +
                    " doesn't belong to board with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteBoardById(UUID id, User user) {
        Board board = getExistingBoard(id);

        validateOwnership(board, user);

        boardRepo.deleteById(id);
        log.info("Board with id: {} was deleted", id);
    }

    @Override
    public void validatePermissions(Board board, User currentUser) {
        if(!checkIfCreator(board, currentUser) && !checkIfMember(board, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), board.getId());
        }
    }

    @Override
    public void validateOwnership(Board board, User currentUser) {
        if(!checkIfCreator(board, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), board.getId());
        }
    }

    @Override
    public List<BoardResponse> getMemberedBoardsByUserId(UUID id) {
        return boardRepo.findByMemberId(id).stream()
                .map(boardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardResponse> getCreatedBoardsByUserId(UUID id) {
        return boardRepo.findByCreatorId(id).stream()
                .map(boardMapper::toDto)
                .collect(Collectors.toList());
    }

    private Set<User> createMembersFromDto(AddBoardRequest addBoardRequest) {
        if(addBoardRequest.getMemberIds() == null || addBoardRequest.getMemberIds().isEmpty()) {
            return new HashSet<>();
        } else {
            return userService.getUsersByIdIn(addBoardRequest.getMemberIds()).stream()
                    .map(userMapper::toEntity)
                    .collect(Collectors.toSet());
        }
    }
    private Board getExistingBoard(UUID id) {
        return boardRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private boolean checkIfCreator(Board board, User currentUser) {
        return Objects.equals(board.getCreator().getId(), currentUser.getId());
    }

    private boolean checkIfMember(Board board, User currentUser) {
        return board.getMembers().stream()
                .anyMatch(member -> Objects.equals(member.getId(), currentUser.getId()));
    }

    private void enrichBoard(Board board) {
        board.setMembers(new HashSet<>());
        board.setTasks(new HashSet<>());
    }
}
