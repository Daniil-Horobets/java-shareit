package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemOwnerMismatchException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long owner) {
        if (userDao.getAllUsers().stream().noneMatch(user -> user.getId() == owner)) {
            throw new ItemOwnerMismatchException("User specified as item owner does not exist");
        }
        return itemDao.createItem(itemDto, owner);
    }

    @Override
    public ItemDto updateItem(long itemId, long owner, ItemDto itemDto) {
        return itemDao.updateItem(itemId, owner, itemDto);
    }

    @Override
    public ItemDto getItem(long id) {
        return itemDao.getItem(id);
    }

    @Override
    public List<ItemDto> getUserItems(long owner) {
        return itemDao.getUserItems(owner);
    }

    @Override
    public List<ItemDto> findItem(String text) {
        return itemDao.findItem(text);
    }
}
